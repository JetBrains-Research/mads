package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.synapses.SynapseMechanisms
import org.jetbrains.research.mads_ns.synapses.SynapseObject
import org.jetbrains.research.mads_ns.synapses.SynapseParamsSaveToFile

fun synapsePathway() = pathway<SynapseObject> {
    mechanism(mechanism = SynapseMechanisms.SpikeTransfer, SynapseParamsSaveToFile) {
        duration = 2
    }
//    mechanism(mechanism = SynapseMechanisms.SynapseDecay, SynapseParamsNoSave) {
//        duration = 10
//    }
}