package org.jetbrains.research.mads.core.telemetry

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject
import kotlin.reflect.KProperty

object EmptySaver : Saver {
    override fun addSignalsNames(signal: KProperty<*>) { }
    override fun logChangedState(tick: Long, obj: ModelObject) { }
    override fun logState(model: Model) { }
}