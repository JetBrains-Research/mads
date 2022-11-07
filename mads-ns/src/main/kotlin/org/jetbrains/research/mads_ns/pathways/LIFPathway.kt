package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFParamsSaveToFile

fun lifPathway() = pathway<Neuron> {
    mechanism(mechanism = LIFMechanisms.VDynamic, parameters = LIFParamsSaveToFile) {
        duration = 2
        condition = Always
    }
}