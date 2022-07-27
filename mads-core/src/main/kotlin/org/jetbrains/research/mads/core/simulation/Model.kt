package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.domain.*
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.wrapMechanism


fun main() {
    val s = Model()
    s.simulate()
}

class Model {
    public fun init() {
        //TODO: here we process initial responses and create initial model state from Ø to S_0
        // Should be constructor
    }

    public fun simulate(/*TODO insert stopCondition*/) {
        val simple = SimpleObject()
        val dummy = DummyObject()
        wrapMechanism(simple, SimpleObject::simpleMechanism, SimpleParameters(0.5))
        wrapMechanism(dummy, DummyObject::simpleMechanism, SimpleParameters(0.8))

        val dispatcher = EventsDispatcher<ModelObject>()
        simple.events.forEach { it.prepareEvent() }
        dummy.events.forEach { it.prepareEvent() }

        dispatcher.addEvents(arrayOf(simple.events.toTypedArray(), dummy.events.toTypedArray()).flatten().toTypedArray())
        val responses = dispatcher.calculateNextTick()
        responses.forEach { println( (it as SimpleResponse).response) }

        //TODO: steps
        // 1. process events from queue -> get responses
        // 2. group responses by objects -> map of responses
        // 3. apply responses to each object independently -> S_i to S_i+1
        // 4. calculate conditions -> map of events
        // 5. update events in queue -> S_t
        // 6. check S_i for stop condition -> stop or repeat from 1
    }
}