package org.jetbrains.research.mads.ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.ns.physiology.neurons.IzhMechanisms
import org.jetbrains.research.mads.ns.physiology.neurons.IzhNeuron
import org.jetbrains.research.mads.ns.physiology.neurons.NeuronMechanisms
import org.jetbrains.research.mads.ns.physiology.neurons.SpikeTransferConstants

fun izhPathway() = pathway<IzhNeuron> {
    timeResolution = microsecond
    mechanism(mechanism = IzhMechanisms.VDynamic) {
        duration = 100
        condition = Always
    }
    mechanism(mechanism = IzhMechanisms.UDynamic) {
        duration = 100
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
        duration = 100
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
        duration = 100
        condition = { underThresholdAndSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
        duration = 100
        condition = { overThresholdAndNotSpiked(it) }
        constants = SpikeTransferConstants()
    }
}