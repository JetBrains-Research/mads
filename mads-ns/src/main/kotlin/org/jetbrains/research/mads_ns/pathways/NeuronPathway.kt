package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.NeuronMechanisms
import org.jetbrains.research.mads_ns.physiology.neurons.PotentialSignals
import org.jetbrains.research.mads_ns.physiology.neurons.SpikesSignals
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParamsNoSave
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParamsSaveToFile

fun neuronPathway() = pathway<Neuron> {
    mechanism(mechanism = NeuronMechanisms.IDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn, parameters = HHParamsSaveToFile) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOff, parameters = HHParamsSaveToFile) {
        duration = 1
        condition = { underThresholdAndSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer, parameters = HHParamsSaveToFile) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.STDPDecay, parameters = HHParamsNoSave) {
        duration = 10
        condition = Always
    }
}

private fun overThresholdAndNotSpiked(neuron: Neuron) : Boolean {
    val spikesSignals = neuron.signals[SpikesSignals::class] as SpikesSignals
    val potentialSignals = neuron.signals[PotentialSignals::class] as PotentialSignals

    return potentialSignals.V > spikesSignals.spikeThreshold && !spikesSignals.spiked
}

private fun underThresholdAndSpiked(neuron: Neuron) : Boolean {
    val spikesSignals = neuron.signals[SpikesSignals::class] as SpikesSignals
    val potentialSignals = neuron.signals[PotentialSignals::class] as PotentialSignals

    return potentialSignals.V < spikesSignals.spikeThreshold && spikesSignals.spiked
}