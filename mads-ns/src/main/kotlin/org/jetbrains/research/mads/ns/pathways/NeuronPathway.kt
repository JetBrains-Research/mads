package org.jetbrains.research.mads.ns.pathways

import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.ns.physiology.neurons.PotentialSignals
import org.jetbrains.research.mads.ns.physiology.neurons.SpikesSignals

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

fun spiked(neuron: Neuron) : Boolean {
    val spikesSignals = neuron.signals[SpikesSignals::class] as SpikesSignals

    return spikesSignals.spiked
}