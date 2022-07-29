package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.configuration.MocRecord
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent

abstract class ModelObject {
    open val type = "Model Object"

    var events : List<ModelEvent<*>> = ArrayList()

    fun addEvents(e : List<ModelEvent<*>>) {
        events = e
    }

    fun <MO : ModelObject> createEvents(pathway: Pathway<ModelObject>): Unit {
        val events = ArrayList<ModelEvent<ModelObject>>()
        pathway.mocRecords.forEach {
            val event = ModelEvent(it.mechanism, it.condition, this, it.duration)
            events.add(event) }
    }

    fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }
}