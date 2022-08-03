package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.ObjectStorage
import org.jetbrains.research.mads.core.types.Response
import java.util.*
import java.util.stream.Collectors

class Model(private val objects : List<ModelObject>,
            private val configuration: Configuration,
            private val objectStorage: ObjectStorage) {
    private val dispatcher = EventsDispatcher()

    fun init() {
        //TODO: here we process initial responses and create initial model state from Ø to S_0
        // Should be constructor
        objects.forEach {
            objectStorage.addObject(it)
            configuration.createEvents(it)
            it.checkConditions()
        }

        val allEvents = objects.map { it.events.toTypedArray() }.toTypedArray().flatten().toTypedArray()
        dispatcher.addEvents(allEvents)
    }

    fun simulate(stopCondition: (Model) -> Boolean) {

        // 0. check S_i for stop condition -> stop or repeat from 1
        while (!stopCondition(this)) {

            // 1. process events from queue -> get responses
            val responses = dispatcher.calculateNextTick()

            // 2. group responses by objects -> map of responses
            val groupedResponses : Map<ModelObject, List<Response>> = Arrays.stream(responses)
                .parallel()
                .collect(Collectors.groupingBy(Response::sourceObject))

            // 3. apply responses to each object independently -> S_i to S_i+1
            val updatedObjects = groupedResponses.entries.parallelStream()
                .map { e -> e.key.applyResponses(e.value) }
                .toArray<Array<ModelObject>?> { length -> arrayOfNulls(length) }
                .flatten().distinct().toTypedArray()

            // 4. calculate conditions -> map of events
            updatedObjects.forEach {
                configuration.createEvents(it)
                it.checkConditions()
            }

            // 5. update events in queue -> S_t
            val allEvents = updatedObjects.map { it.events.toTypedArray() }.toTypedArray().flatten().toTypedArray()
            dispatcher.addEvents(allEvents)
        }
    }

    fun currentTime(): Long {
        return dispatcher.peekHead()
    }
}