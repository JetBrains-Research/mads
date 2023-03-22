package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.pathways.connectToCell
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseSignals

fun createPopulation(capacity: Int, type: String, neuronFun: () -> Neuron) : List<Neuron> {
    val population: ArrayList<Neuron> = arrayListOf()
    for (i in 0 until capacity) {
        val neuron = neuronFun()
        neuron.type = type
        population.add(neuron)
    }

    return population
}

fun connectElectrodes(population: List<Neuron>, electrodeFn: (Long) -> Electrode) : List<Electrode> {
    val electrodes: ArrayList<Electrode> = arrayListOf()
    for (i in population.indices) {
        val electrode : Electrode = electrodeFn(12345L + i)
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
    currentSignals: CurrentSignals,
    synapseSignals: SynapseSignals
): Synapse {
    val synapse = Synapse(releaser, receiver, inhibitory, currentSignals, synapseSignals)
    receiver.addConnection(synapse, SynapseReceiver)
    releaser.addConnection(synapse, SynapseReleaser)

    return synapse
}

fun connectPopulations(source: List<Neuron>, destination: List<Neuron>, weight: () -> Double = { 1.0 }) : List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for(i in source.indices) {
        for (j in destination.indices) {
            val syn = connectCellsWithSynapse(
                source[i],
                destination[j],
                false,
                CurrentSignals(0.0),
                SynapseSignals(weight = weight())
            )
            synapses.add(syn)
        }
    }

    return synapses
}