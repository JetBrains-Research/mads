package org.jetbrains.research.mads_ns.hh

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class HHCell(current: CurrentSignals, hh: HHSignals, val constantCurrent: Boolean = true) : SignalsObject(current, hh) {
    fun updateI(delta: Double) {
        val sig = this.signals[CurrentSignals::class] as CurrentSignals
        sig.I_e = delta
    }

    fun updateV(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.V += delta
    }

    fun updateN(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.N += delta
    }

    fun updateM(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.M += delta
    }

    fun updateH(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.H += delta
    }

    fun updateIexternal(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.I_ext += delta
    }
}

data class CurrentSignals(
    var I_e: Double = 8.0,
) : Signals {
    override fun clone() : Signals
    {
        return this.copy()
    }
}

data class HHSignals(
    var V: Double = -65.0,
    var N: Double = 0.32,
    var M: Double = 0.05,
    var H: Double = 0.6,
    var I_ext: Double = 0.0
) : Signals {
    override fun clone() : Signals
    {
        return this.copy()
    }
}