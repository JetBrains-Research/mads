package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.synapses.*

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
            currentSignals.I_e > 0.01
        }
    }
//    mechanism(mechanism = SynapseMechanisms.STDUpdate) {
//        duration = 10
//    }
}

fun connectCellsWithSynapse(
    releaser: ModelObject,
    receiver: ModelObject,
    inhibitory: Boolean,
    currentSignals: CurrentSignals,
    synapseSignals: SynapseSignals
): Synapse {
    val synapse = Synapse(releaser, receiver, inhibitory, currentSignals, synapseSignals)
    receiver.addConnection(synapse, SynapseReceiver)
    releaser.addConnection(synapse, SynapseReleaser)

    return synapse
}