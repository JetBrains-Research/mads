package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.desd.ModelEvent
import org.jetbrains.research.mads.core.types.*

class Pathway<MO : ModelObject> {
    private val mocRecords = ArrayList<MocRecord<MO>>()

    fun add(mechanism: (MO) -> Array<Response>, duration: Int, condition: (MO) -> Boolean) {
        mocRecords.add(MocRecord(mechanism, duration, condition))
    }

    fun createEvents(obj: MO): List<ModelEvent<MO>> {
        val events = ArrayList<ModelEvent<MO>>()
        mocRecords.forEach {
            val event = ModelEvent(it.mechanism, it.condition, obj, it.duration)
            events.add(event) }

        return events
    }
}