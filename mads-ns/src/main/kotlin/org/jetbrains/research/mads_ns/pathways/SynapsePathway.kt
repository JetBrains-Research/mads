package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads_ns.hh.HHCell
import org.jetbrains.research.mads_ns.synapses.*

fun synapsePathway() = pathway<Synapse> {
//    mechanism(mechanism = SynapseMechanisms.SynapseDecay, SynapseParamsNoSave) {
//        duration = 100
//    }
    mechanism(mechanism = SynapseMechanisms.STDUpdate, SynapseParamsNoSave) {
        duration = 10
    }

}

fun connectCellsWithSynapse(
        releaser: SignalsObject,
        receiver: SignalsObject,
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