package org.jetbrains.research.mads.ns

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.ElectrodeArray
import org.jetbrains.research.mads.ns.electrode.connectToCell
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.ns.physiology.neurons.PotentialSignals
import org.jetbrains.research.mads.ns.physiology.neurons.STDPTripletSignals
import org.jetbrains.research.mads.ns.physiology.neurons.SpikesSignals
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals
import java.util.*

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

fun createPopulation(capacity: Int, type: String, neuronFun: () -> Neuron): List<Neuron> {
    val population: ArrayList<Neuron> = arrayListOf()
    for (i in 0 until capacity) {
        val neuron = neuronFun()
        neuron.type = type
        population.add(neuron)
    }

    return population
}

fun connectElectrodes(population: List<Neuron>, electrodeFn: (Long) -> Electrode): List<Electrode> {
    val electrodes: ArrayList<Electrode> = arrayListOf()
    for (i in population.indices) {
        val electrode: Electrode = electrodeFn(12345L + i)
        electrode.connectToCell(population[i])
        electrodes.add(electrode)
    }

    return electrodes
}

fun connectElectrodeArray(electrodeArray: ElectrodeArray, population: List<Neuron>) {
    if (electrodeArray.capacity() != population.size) {
        val exceptionString = String.format("Size of electrode array and population of neurons doesn't match!")
        throw RuntimeException(exceptionString)
    }

    for (i in population.indices) {
        val electrode = electrodeArray[i]
        electrode.connectToCell(population[i])
    }
}

fun connectCellsWithSynapse(
    releaser: ModelObject,
    receiver: ModelObject,
    inhibitory: Boolean,
    synapseSignals: SynapseSignals,
    vararg signals: Signals
): Synapse {
    val synapse = Synapse(releaser, receiver, inhibitory, synapseSignals, *signals)
    receiver.addConnection(synapse, SynapseReceiver)
    releaser.addConnection(synapse, SynapseReleaser)

    return synapse
}

fun connectPopulations(
    source: List<Neuron>,
    destination: List<Neuron>,
    weight: () -> Double = { 1.0 },
    delay: () -> Int = { 0 },
    probability: Double = 1.0,
    rnd: Random = Random(42L)
): List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for (i in source.indices) {
        for (j in destination.indices) {

            if (rnd.nextDouble() > probability) {
                continue
            }

            val syn = connectCellsWithSynapse(
                source[i],
                destination[j],
                false,
                SynapseSignals(weight = weight(), delay = delay(), maxWeight = 1.0, learningEnabled = false),
                STDPTripletSignals()
            )
            syn.type = "syn_e"
            synapses.add(syn)
        }
    }

    return synapses
}

fun connectPopulationsInhibition(
    source: List<Neuron>,
    destination: List<Neuron>,
    weight: () -> Double = { 1.0 },
    delay: () -> Int = { 0 }
): List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for (i in source.indices) {
        for (j in destination.indices) {
            if (i == j) {
                continue
            }
            val syn = connectCellsWithSynapse(
                source[i],
                destination[j],
                true,
                SynapseSignals(weight = weight(), delay = delay(), maxWeight = 1.0, learningEnabled = false),
                STDPTripletSignals()
            )
            syn.type = "syn_i"
            synapses.add(syn)
        }
    }

    return synapses
}

fun connectPopulationsOneToOne(
    source: List<Neuron>,
    destination: List<Neuron>,
    weight: () -> Double = { 1.0 },
    delay: () -> Int = { 0 }
): List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for (i in source.indices) {
        val syn = connectCellsWithSynapse(
            source[i],
            destination[i],
            false,
            SynapseSignals(weight = weight(), delay = delay(), maxWeight = 4.0, learningEnabled = false),
            STDPTripletSignals()
        )
        syn.type = "syn_e"
        synapses.add(syn)
    }

    return synapses
}