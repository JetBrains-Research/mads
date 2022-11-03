package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.NeuronMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHNeuron
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParamsNoSave
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParamsSaveToFile

fun neuronPathway() = pathway<Neuron> {
    mechanism(mechanism = NeuronMechanisms.IDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer, parameters = HHParamsSaveToFile) {
        duration = 2
    }
    mechanism(mechanism = NeuronMechanisms.STDPDecay, parameters = HHParamsNoSave) {
        duration = 10
        condition = Always
    }
}