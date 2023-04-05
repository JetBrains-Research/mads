package org.jetbrains.research.mads.ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.ns.physiology.neurons.NeuronMechanisms
import org.jetbrains.research.mads.ns.physiology.neurons.PotentialSignals
import org.jetbrains.research.mads.ns.physiology.neurons.SpikesSignals

fun neuronPathway() = pathway<Neuron> {
    mechanism(mechanism = NeuronMechanisms.IDynamic) {
        duration = 2
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

fun stdpPathway() = pathway<Neuron> {
    mechanism(mechanism = NeuronMechanisms.STDPDecay) {
        duration = 10
        condition = Always
    }
}

fun overThresholdAndNotSpiked(neuron: Neuron) : Boolean {
    val spikesSignals = neuron.signals[SpikesSignals::class] as SpikesSignals
    val potentialSignals = neuron.signals[PotentialSignals::class] as PotentialSignals

    return potentialSignals.V > spikesSignals.spikeThreshold && !spikesSignals.spiked
}

fun underThresholdAndSpiked(neuron: Neuron) : Boolean {
    val spikesSignals = neuron.signals[SpikesSignals::class] as SpikesSignals
    val potentialSignals = neuron.signals[PotentialSignals::class] as PotentialSignals

    return potentialSignals.V < spikesSignals.spikeThreshold && spikesSignals.spiked
}