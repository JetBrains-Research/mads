package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHNeuron

fun hhPathway() = pathway<HHNeuron> {
    mechanism(mechanism = HHMechanisms.VDynamic) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.NDynamic) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.MDynamic) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.HDynamic) {
        duration = 2
        condition = Always
    }
}