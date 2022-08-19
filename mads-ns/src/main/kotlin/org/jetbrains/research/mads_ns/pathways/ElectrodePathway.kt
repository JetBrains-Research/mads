package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads_ns.electrode.ElectrodeParametersNoSave
import org.jetbrains.research.mads_ns.synapses.SynapseMechanisms
import org.jetbrains.research.mads_ns.synapses.SynapseObject
import org.jetbrains.research.mads_ns.synapses.SynapseParamsSaveToFile

fun electrodePathway() = pathway<Electrode> {
    mechanism(mechanism = ElectrodeMechanisms.PulseDynamic, ElectrodeParametersNoSave(0.5, 50.0)) {
        duration = 100
    }
}