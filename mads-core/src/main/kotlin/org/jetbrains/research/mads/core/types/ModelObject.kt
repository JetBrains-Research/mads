package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.desd.ModelEvent

abstract class ModelObject {
    open val type = "Model Object"

    var events : List<ModelEvent<*>> = ArrayList()

    fun addEvents(e : List<ModelEvent<*>>) {
        events = e
    }

    fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }
}