package org.jetbrains.research.mads.ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.ns.physiology.neurons.HHMechanisms
import org.jetbrains.research.mads.ns.physiology.neurons.HHNeuron
import org.jetbrains.research.mads.ns.physiology.neurons.NeuronMechanisms
import org.jetbrains.research.mads.ns.physiology.neurons.SpikeTransferConstants

fun hhPathway() = pathway<HHNeuron> {
    timeResolution = microsecond
    mechanism(mechanism = HHMechanisms.VDynamic) {
        duration = 25
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.NDynamic) {
        duration = 25
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.MDynamic) {
        duration = 25
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.HDynamic) {
        duration = 25
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.IDynamic) {
        duration = 25
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
        duration = 25
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
        duration = 25
        condition = { underThresholdAndSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
        duration = 25
        condition = { overThresholdAndNotSpiked(it) }
        constants = SpikeTransferConstants()
    }
}