package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads.core.telemetry.Saver
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.stream.Collectors

object RootObject : ModelObject()

class Model private constructor(
    objects: List<ModelObject>,
    private val configuration: Configuration
) : ModelObject() {

    private var tStart: Long = 0
    private val dispatcher = EventsDispatcher()
    private val progressBar: ProgressBarRotating = ProgressBarRotating(250, "step: 0")

    init {
        println("Simulation step size is equal to ${configuration.timeResolution} seconds")
        progressBar.start()
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

    fun simulate(saver: Saver = EmptySaver, stopCondition: (Model) -> Boolean) {
        tStart = System.currentTimeMillis()
        var lastStep = 0L

        // 0. check S_i for stop condition -> stop or repeat from 1
        while (!stopCondition(this)) {

            // 1. process events from queue -> get grouped responses by model object
            val currentTime = currentTime()
            val responses = dispatcher.calculateNextTick()

            // 2. apply responses to each object independently -> S_i to S_i+1
            val updatedObjects = responses.entries.parallelStream()
                .map { e -> e.key.applyResponses(e.value) }
                .flatMap { it.stream() }
                .distinct()
                .collect(Collectors.toList())

            // 3. calculate conditions -> map of events
            updatedObjects.parallelStream()
                .forEach {
                    saver.logChangedSignals(currentTime, it.hashCode(), it.type, it.getChangedSignals())
                    if (!it.initialized) configuration.createEvents(it)
                    it.checkConditions()
                }

            // 4. update events in queue -> S_t
            val allEvents = updatedObjects.parallelStream()
                .map { it.events }
                .flatMap { it.stream() }
                .collect(Collectors.toList())

            dispatcher.addEvents(allEvents)

            val realTime = System.currentTimeMillis() - tStart
            val extInfo = "step: ${"%,d".format(currentTime)}"
            lastStep = currentTime
            progressBar.updateInfo(realTime, extInfo)
        }
        progressBar.stop("done")
        val totalModelingTime = configuration.timeResolution.toBigDecimal().multiply(lastStep.toBigDecimal()).toDouble()
        println("Total of $totalModelingTime seconds were simulated\n")
    }

    fun currentTime(): Long {
        return dispatcher.peekHead()
    }

    companion object {
        operator fun invoke(objects: List<ModelObject>, configuration: Configuration): Model? {
            if (configuration.hasErrors()) {
                configuration.errors().forEach { println(it) }
                return null
            }

            if (objects.isEmpty()) {
                println("Nothing to simulate. Initial model state does not contain any objects.")
                return null
            }

            return Model(objects, configuration)
        }
    }
}