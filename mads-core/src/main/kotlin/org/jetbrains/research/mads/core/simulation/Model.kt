package org.jetbrains.research.mads.core.simulation

class Model {
    public fun init() {
        //TODO: here we process initial responses and create initial model state from Ø to S_0
    }

    public fun simulate() {
        //TODO: steps
        // 1. process events from queue -> get responses
        // 2. group responses by objects -> map of responses
        // 3. apply responses to each object independently -> S_i to S_i+1
        // 4. calculate conditions -> map of events
        // 5. update events in queue -> S_t
        // 6. check S_t for stop condition -> stop or repeat from 1
    }
}