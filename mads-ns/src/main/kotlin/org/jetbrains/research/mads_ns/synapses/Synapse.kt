package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals

class Synapse(
    var releaser: SignalsObject,
    var receiver: SignalsObject,
    isInhibitory: Boolean = false,
    current: CurrentSignals,
    synapse: SynapseSignals
) : SignalsObject(current, synapse) {

    init {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals
        connections[SynapseReleaser] = hashSetOf(releaser)
        connections[SynapseReceiver] = hashSetOf(receiver)

        if (isInhibitory) {
            sig.synapseSign = -1.0
        }
    }

    fun updateI(delta: Double) {
        val sig = this.signals[CurrentSignals::class] as CurrentSignals
        sig.I_e = delta
    }

    fun updateWeight(newValue: Double) {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals
        sig.weight = newValue
    }
}

data class SynapseSignals(
    var weight: Double = 1.0,
    var synapseSign: Double = 1.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}