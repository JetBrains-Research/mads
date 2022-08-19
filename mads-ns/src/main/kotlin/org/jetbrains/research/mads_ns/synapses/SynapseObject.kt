package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads_ns.hh.HHCellObject

class SynapseObject(
    var objectLeft: HHCellObject,
    var objectRight: HHCellObject,
    isInhibitory: Boolean = false,
    vararg signals: Signals
) : SignalsObject(*signals) {

    init {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals

        if (isInhibitory) {
            sig.synapseSign = -1.0
        }
    }

    fun updateSpiked(newValue: Boolean) {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals
        sig.spiked = newValue
    }

    fun updateWeight(newValue: Double) {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals
        sig.weight = newValue
    }
}

data class SynapseSignals(
    var weight: Double = 1.0,
    var spiked: Boolean = false,
    var spikeThreshold: Double = 25.0,
    var synapseSign: Double = 1.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}