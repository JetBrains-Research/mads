package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads_ns.physiology.neurons.NeuronMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhNeuron

fun izhPathway() = pathway<IzhNeuron> {
    mechanism(mechanism = IzhMechanisms.VDynamic) {
        duration = 2
        condition = Always
//        logFn = FileSaver::logResponse
    }
    mechanism(mechanism = IzhMechanisms.UDynamic) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.IDynamic) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
        logFn = FileSaver::logResponse
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