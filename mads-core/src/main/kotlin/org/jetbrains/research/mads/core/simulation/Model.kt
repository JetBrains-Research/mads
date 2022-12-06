package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.stream.Collectors

object RootObject : ModelObject()

class Model(
    objects: List<ModelObject>,
    private val configuration: Configuration
) : ModelObject() {

    private var tStart: Long = 0
    private val dispatcher = EventsDispatcher()
    private val progressBar: ProgressBarRotating = ProgressBarRotating(250, "step: 0")

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
        progressBar.start()
        tStart = System.currentTimeMillis()

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
            val extInfo = "step: $currentTime"
            progressBar.updateInfo(realTime, extInfo)
        }
        progressBar.stop("done")
    }

    fun currentTime(): Long {
        return dispatcher.peekHead()
    }
}