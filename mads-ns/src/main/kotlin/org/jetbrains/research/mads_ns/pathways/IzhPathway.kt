package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParamsNoSave
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhRSParamsNoSave

fun izhPathway() = pathway<Neuron> {
    mechanism(mechanism = IzhMechanisms.VDynamic, parameters = IzhRSParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = IzhMechanisms.UDynamic, parameters = IzhRSParamsNoSave) {
        duration = 2
        condition = Always
    }
}