package org.jetbrains.research.mads.core.telemetry

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject
import kotlin.reflect.KProperty

interface Saver {
    fun addSignalsNames(signal: KProperty<*>)
    fun addObjectTypes(type: String)
    fun logChangedState(tick: Long, obj: ModelObject)
    fun logState(model: Model)
}