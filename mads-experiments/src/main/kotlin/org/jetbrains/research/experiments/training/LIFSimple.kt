package org.jetbrains.research.experiments.training

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.core.types.minute
import org.jetbrains.research.mads_ns.data_provider.MnistProvider
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.pathways.*
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.LIFConstants
import org.jetbrains.research.mads_ns.physiology.neurons.LIFNeuron
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseSignals

fun main() {
    simpleLIFLearning()
}

fun simpleLIFLearning() {
    val saver = FileSaver("log/lifSimpleLearning/${System.currentTimeMillis()}/")

    val modelingTime = 10 * minute
    val nExc = 64

    val targetClasses = arrayListOf("1", "3")
    // TODO use relative path
    val provider = MnistProvider("C:\\projects\\mads\\mads_data\\MNIST_training\\", targetClasses)
    val electrodesArray = ElectrodeArray(provider, 25.0)

    val objects: ArrayList<ModelObject> = arrayListOf()

    objects.add(electrodesArray)

    val firstLayer: ArrayList<LIFNeuron> = arrayListOf()
    val secondLayer: ArrayList<LIFNeuron> = arrayListOf()

    val synapses: ArrayList<Synapse> = arrayListOf()

    for(i in 0 until provider.width) {
        for(j in 0 until provider.height) {
            val cell = LIFNeuron(LIFConstants.V_thresh)
            val electrode = electrodesArray.getElectrodeByCoordinate(i, j)
            electrode.connectToCell(cell)

            firstLayer.add(cell)
        }
    }

    for(i in 0 until nExc) {
        val cell = LIFNeuron(LIFConstants.V_thresh)

        secondLayer.add(cell)
    }

    for(i in 0 until firstLayer.size) {
        for(j in 0 until secondLayer.size) {
            val weight = 1.0
            val syn = connectCellsWithSynapse(firstLayer[i], secondLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
            synapses.add(syn)
        }
    }

    objects.addAll(firstLayer)
    objects.addAll(secondLayer)
    objects.addAll(synapses)

    val config = configure {
        timeResolution = millisecond
        addPathway(synapsePathway())
        addPathway(electrodeArrayPathway())
        addPathway(lifPathway())
    }

    val s = Model(objects, config)

    s?.simulate(saver) { it.currentTime() > modelingTime }
    saver.closeModelWriters()
}
