package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParamsNoSave

fun hhPathway() = pathway<Neuron> {
    mechanism(mechanism = HHMechanisms.VDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.NDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.MDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.HDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
}