package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.physiology.neurons.LIFMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.LIFNeuron
import org.jetbrains.research.mads_ns.physiology.neurons.NeuronMechanisms

fun lifPathway() = pathway<LIFNeuron> {
    timeResolution = millisecond
    mechanism(mechanism = LIFMechanisms.VDynamic) {
        duration = 1
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.IDynamic) {
        duration = 1
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
        duration = 1
        condition = { underThresholdAndSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
    }
}