package org.jetbrains.research.mads.core.simulation

import me.tongfei.progressbar.ConsoleProgressBarConsumer
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.stream.Collectors

object RootObject : ModelObject()

class Model(
    objects: List<ModelObject>,
    private val configuration: Configuration
) : ModelObject() {

    private val dispatcher = EventsDispatcher()

    // TODO: proper use of progress bar, maybe spinner instead: we don't know when stop condition will be true
    private val progressBar: ProgressBar = ProgressBarBuilder()
        .setStyle(ProgressBarStyle.ASCII)
        .setTaskName("Simulation")
        .continuousUpdate()
        .setConsumer(ConsoleProgressBarConsumer(System.out))
        .build()

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
    }

    fun simulate(stopCondition: (Model) -> Boolean) {

        // 0. check S_i for stop condition -> stop or repeat from 1
        while (!stopCondition(this)) {

            // 1. process events from queue -> get grouped responses by model object
            val currentTime = currentTime()
            val responses = dispatcher.calculateNextTick()

            // 2. apply responses to each object independently -> S_i to S_i+1
            val updatedObjects = responses.entries.parallelStream()
                .map { e -> e.key.applyResponses(currentTime, e.value) }
                .flatMap { it.stream() }
                .distinct()
                .collect(Collectors.toList())

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
                .collect(Collectors.toList())

            dispatcher.addEvents(allEvents)

            progressBar.stepTo(currentTime())
        }
        progressBar.extraMessage = "Done"
        progressBar.close()
    }

    fun currentTime(): Long {
        return dispatcher.peekHead()
    }
}