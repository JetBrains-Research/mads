package org.jetbrains.research.mads.ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseMechanisms

fun synapsePathway() = pathway<Synapse> {
    timeResolution = millisecond
//    mechanism(mechanism = SynapseMechanisms.WeightDecay) {
//        duration = 100
//        constants = WeightDecayConstants()
//    }
    mechanism(mechanism = SynapseMechanisms.CurrentDecay) {
        duration = 1
        condition = {
            val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
            currentSignals.I_e > 0.01 || currentSignals.I_e < -0.01
        }
    }
    mechanism(mechanism = SynapseMechanisms.STDUpdate) {
        duration = 1
    }
}

