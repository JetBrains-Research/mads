package org.jetbrains.research.mads_ns.lif

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads.core.types.SignalsObject

class LIFCell(current: CurrentSignals, lif: LIFSignals, val constantCurrent: Boolean = true, val stdpDecayCoefficient: Double=0.99) : SignalsObject(current, lif)
{
    fun updateI(delta: Double) {
        val sig = this.signals[CurrentSignals::class] as CurrentSignals
        sig.I_e = delta
    }

    fun updateV(delta: Double) {
        val sig = this.signals[LIFSignals::class] as LIFSignals

        sig.V += delta
    }

    fun updateSpiked(newValue: Boolean) {
        val sig = this.signals[LIFSignals::class] as LIFSignals
        sig.spiked = newValue
    }

    fun updateSTDPTrace(delta: Double)
    {
        val sig = this.signals[LIFSignals::class] as LIFSignals
        sig.stdpTrace += delta
    }

}

data class LIFSignals(
        var V: Double = -65.0,
        var spiked: Boolean = false,
        var spikeThreshold: Double = LIFConstants.V_thresh - 2.5,
        var stdpTrace: Double = 0.0
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}