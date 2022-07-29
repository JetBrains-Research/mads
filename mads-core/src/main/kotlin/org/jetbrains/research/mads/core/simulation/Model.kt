package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.domain.*
import org.jetbrains.research.mads.core.types.parametrize


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

        val pathwaySimple : Pathway<SimpleObject> = Pathway()
        val pathwayDummy : Pathway<DummyObject> = Pathway()
        val paramsForSimple = SimpleParameters(0.5)
        val paramsForDummy = SimpleParameters(0.8)
        val mechanismSimple = parametrize(SimpleObject::simpleMechanism, paramsForSimple)
        val mechanismDummy = parametrize(DummyObject::simpleMechanism, paramsForDummy)
        pathwaySimple.add(mechanismSimple, paramsForSimple.duration, SimpleObject::simpleCondition)
        pathwayDummy.add(mechanismDummy, paramsForDummy.duration, DummyObject::dummyCondition)

        simple.addEvents(pathwaySimple.createEvents(simple))
        dummy.addEvents(pathwayDummy.createEvents(dummy))

        val dispatcher = EventsDispatcher()
        simple.checkConditions()
        dummy.checkConditions()

        dispatcher.addEvents(arrayOf(simple.events.toTypedArray(), dummy.events.toTypedArray()).flatten().toTypedArray())
        println(dispatcher.peekHead())
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