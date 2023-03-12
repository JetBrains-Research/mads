package org.jetbrains.research.mads.core.telemetry

import org.jetbrains.research.mads.core.simulation.Model
import kotlin.reflect.KProperty

object EmptySaver : Saver {
    override fun addSignalsNames(signal: KProperty<*>) { }
    override fun logChangedSignals(tick: Long, id: Int, type: String, signals: Map<String, String>) { }
    override fun logState(model: Model) { }
}