package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent

abstract class ModelObject(id: Long) {
    open val type = "Model Object"

    var events : ArrayList<ModelEvent> = ArrayList()
    var id: Long = id

    fun <MO : ModelObject> createEvents(pathway: Pathway<MO>) {
        pathway.mocRecords.forEach {
            val thisMO = this as MO
            val mch = applyObjectToMechanism(it.mechanism, thisMO)
            val cnd = applyObjectToCondition(it.condition, thisMO)
            val event = ModelEvent(mch, cnd, it.duration)
            events.add(event) }
    }

    fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }
}