package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.synapses.*

fun synapsePathway() = pathway<Synapse> {
    timeResolution = microsecond
//    mechanism(mechanism = SynapseMechanisms.SynapseDecay) {
//        duration = 100
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
    val synapse: Synapse = Synapse(releaser, receiver, inhibitory, currentSignals, synapseSignals)

    if(!receiver.connections.containsKey(SynapseReceiver)) {
        receiver.connections[SynapseReceiver] = HashSet<ModelObject>()
    }

    if(!releaser.connections.containsKey(SynapseReleaser)) {
        releaser.connections[SynapseReleaser] = HashSet<ModelObject>()
    }

    receiver.connections[SynapseReceiver]?.add(synapse)
    releaser.connections[SynapseReleaser]?.add(synapse)

    return synapse
}