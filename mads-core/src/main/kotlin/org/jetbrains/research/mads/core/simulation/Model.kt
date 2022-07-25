package org.jetbrains.research.mads.core.simulation

import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.domain.DummyObject
import org.jetbrains.research.mads.core.domain.SimpleObject
import org.jetbrains.research.mads.core.domain.SimpleParameters
import org.jetbrains.research.mads.core.domain.simpleMechanism
import org.jetbrains.research.mads.core.types.ModelObject
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
        val mech1 = parametrize(::simpleMechanism, SimpleParameters(0.5))
        val mech2 = parametrize(::simpleMechanism, SimpleParameters(0.8))
        mech1(simple)
        mech2(dummy)

//        val mech = parametrizeI(::simpleMechanism, SimpleParameters(0.4))

        EventsDispatcher<ModelObject>()
        //TODO: steps
        // 1. process events from queue -> get responses
        // 2. group responses by objects -> map of responses
        // 3. apply responses to each object independently -> S_i to S_i+1
        // 4. calculate conditions -> map of events
        // 5. update events in queue -> S_t
        // 6. check S_i for stop condition -> stop or repeat from 1
    }
}