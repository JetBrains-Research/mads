package org.jetbrains.research.mads.core.telemetry

import kotlin.reflect.KProperty

object EmptySaver : Saver {
    override fun addSignalsNames(signal: KProperty<*>) { }

    override fun logChangedSignals(tick: Long, id: Int, type: String, signals: Map<String, String>) { }
}