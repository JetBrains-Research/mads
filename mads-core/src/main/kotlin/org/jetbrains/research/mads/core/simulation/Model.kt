package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.types.ModelObject

class Model(private val objects : ArrayList<ModelObject>, private val configuration: Configuration<ModelObject>) {

    public fun init() {
        //TODO: here we process initial responses and create initial model state from Ø to S_0
        // Should be constructor
    }

    public fun simulate(/*TODO insert stopCondition*/) {
        val dispatcher = EventsDispatcher()
        objects.forEach { configuration.createEvents(it) }
        objects.forEach { it.checkConditions() }

        val allEvents = objects.map { it.events.toTypedArray() }.toTypedArray().flatten().toTypedArray()
        dispatcher.addEvents(allEvents)

//        simple.addEvents(pathwaySimple.createEvents(simple))
//        dummy.addEvents(pathwayDummy.createEvents(dummy))
//        simple.checkConditions()
//        dummy.checkConditions()

//        dispatcher.addEvents(arrayOf(simple.events.toTypedArray(), dummy.events.toTypedArray()).flatten().toTypedArray())
//        println(dispatcher.peekHead())
        val responses = dispatcher.calculateNextTick()
        responses.forEach { println( it.response) }

        //TODO: steps
        // 1. process events from queue -> get responses
        // 2. group responses by objects -> map of responses
        // 3. apply responses to each object independently -> S_i to S_i+1
        // 4. calculate conditions -> map of events
        // 5. update events in queue -> S_t
        // 6. check S_i for stop condition -> stop or repeat from 1
    }
}