package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.pathways.connectToCell
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseSignals

fun createPopulation(capacity: Int, neuronFun: () -> Neuron) : List<Neuron> {
    val population: ArrayList<Neuron> = arrayListOf()
    for (i in 0 until capacity) {
        population.add(neuronFun())
    }

    return population
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

fun connectPopulations(source: List<Neuron>, destination: List<Neuron>) : List<Synapse> {
    val synapses: ArrayList<Synapse> = arrayListOf()

    for(i in source.indices) {
        for (j in destination.indices) {
            val weight = 1.0
            val syn = connectCellsWithSynapse(
                source[i],
                source[j],
                false,
                CurrentSignals(0.0),
                SynapseSignals(weight = weight)
            )
            synapses.add(syn)
        }
    }

    return synapses
}