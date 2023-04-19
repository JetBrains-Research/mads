package org.jetbrains.research.mads.ns

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.ElectrodeArray
import org.jetbrains.research.mads.ns.pathways.connectToCell
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals

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

fun connectPopulationsInhibition(source: List<Neuron>, destination: List<Neuron>, weight: () -> Double = { 1.0 }) : List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for(i in source.indices) {
        for (j in destination.indices) {
            if(i == j)
            {
                continue
            }
            val syn = connectCellsWithSynapse(
                source[i],
                destination[j],
                true,
                CurrentSignals(0.0),
                SynapseSignals(weight = weight())
            )
            synapses.add(syn)
        }
    }

    return synapses
}

fun connectPopulationsOneToOne(source: List<Neuron>, destination: List<Neuron>, weight: () -> Double = { 1.0 }) : List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for(i in source.indices) {
        val syn = connectCellsWithSynapse(
            source[i],
            destination[i],
            false,
            CurrentSignals(0.0),
            SynapseSignals(weight = weight())
        )
        synapses.add(syn)
    }

    return synapses
}