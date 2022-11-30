package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFNeuron

fun lifPathway() = pathway<LIFNeuron> {
    mechanism(mechanism = LIFMechanisms.VDynamic) {
        duration = 2
        condition = Always
    }
}