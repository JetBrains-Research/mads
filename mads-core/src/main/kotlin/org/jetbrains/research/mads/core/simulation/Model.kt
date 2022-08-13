package org.jetbrains.research.mads.core.simulation

import me.tongfei.progressbar.ConsoleProgressBarConsumer
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.telemetry.DataBroker
import org.jetbrains.research.mads.core.types.ModelObject
import kotlin.streams.toList


object RootObject : ModelObject()

class Model(
    objects: List<ModelObject>,
    private val configuration: Configuration
) : ModelObject() {

    private val dispatcher = EventsDispatcher()
    private val progressBar: ProgressBar = ProgressBarBuilder()
        .setStyle(ProgressBarStyle.ASCII)
        .setTaskName("Simulation")
        .continuousUpdate()
        .setConsumer(ConsoleProgressBarConsumer(System.out))
        .build()

//    companion object {
//        internal val LOG = LoggerFactory.getLogger("ROOT") as Logger
//
//        init {
//            val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
//            val fileAppender = FileAppender<ILoggingEvent>()
//            fileAppender.context = loggerContext
//            fileAppender.name = "file"
//            // set the file name
//            fileAppender.file = "log/" + System.currentTimeMillis() + ".log"
//
//            val encoder = PatternLayoutEncoder()
//            encoder.context = loggerContext
//            encoder.pattern = "[%d{HH:mm:ss.SSS}] %level: %msg%n"
//            encoder.start()
//
//            fileAppender.encoder = encoder
//            fileAppender.start()
//
//            // attach the rolling file appender to the logger of your choice
////            val logbackLogger = loggerContext.getLogger("ROOT")
//            LOG.detachAndStopAllAppenders()
//            LOG.addAppender(fileAppender)
//
//            LOG.iteratorForAppenders().asSequence().forEach { println(it.name) }
//        }
//    }

    init {
        parent = RootObject
        configuration.createEvents(this)
        this.checkConditions()
        objects.forEach {
            childObjects.add(it)
            it.parent = this
            configuration.createEvents(it)
            it.checkConditions()
        }

        val allEvents = objects.map { it.events }.flatten()
        dispatcher.addEvents(allEvents)

        // log something
//        LOG.info("hello")
    }

    fun simulate(stopCondition: (Model) -> Boolean) {

        // 0. check S_i for stop condition -> stop or repeat from 1
        while (!stopCondition(this)) {

            // 1. process events from queue -> get grouped responses by model object
            val responses = dispatcher.calculateNextTick()
            val currentTime = currentTime()

            // 2. apply responses to each object independently -> S_i to S_i+1
            val updatedObjects = responses.entries.parallelStream()
                .map { e -> e.key.applyResponses(currentTime,e.value) }
                .flatMap { it.stream() }
                .distinct()
                .toList()

            // 3. calculate conditions -> map of events
            updatedObjects.parallelStream()
                .forEach {
                    configuration.createEvents(it)
                    it.checkConditions()
                }

            // 4. update events in queue -> S_t
            val allEvents = updatedObjects.parallelStream()
                .map { it.events }
                .flatMap { it.stream() }
                .toList()

            dispatcher.addEvents(allEvents)

            progressBar.stepTo(currentTime())
        }
        progressBar.extraMessage = "Done"
        progressBar.close()
        DataBroker.INSTANCE.closeModelWriters()
    }

    fun currentTime(): Long {
        return dispatcher.peekHead()
    }
}