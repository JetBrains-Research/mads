package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads_ns.hh.HHCell
import org.jetbrains.research.mads_ns.synapses.*

fun synapsePathway() = pathway<Synapse> {
//    mechanism(mechanism = SynapseMechanisms.SynapseDecay, SynapseParamsNoSave) {
//        duration = 10
//    }
}

fun connectCellsWithSynapse(
    releaser: HHCell,
    receiver: HHCell,
    inhibitory: Boolean,
    currentSignals: CurrentSignals,
    synapseSignals: SynapseSignals
): Synapse {
    val synapse: Synapse = Synapse(releaser, receiver, inhibitory, currentSignals, synapseSignals)
    receiver.connections[SynapseReceiver] = hashSetOf(synapse)
    releaser.connections[SynapseReleaser] = hashSetOf(synapse)

    return synapse
}