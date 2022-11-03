package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.data_provider.MnistProvider
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFNeuron
import org.jetbrains.research.mads_ns.pathways.*
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.synapses.Synapse
import org.jetbrains.research.mads_ns.synapses.SynapseSignals
import java.io.File
import kotlin.random.Random

fun main() {
    createLIFCellsExperiment()
//    createLIFTrainingExperimentExcInhib()
}

fun createLIFCellsExperiment() {
//    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class))
    FileSaver.initModelWriters("log/lif_start/", setOf(SignalDoubleChangeResponse::class))
    val rnd: Random = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1
    for (i in 0 until neuronCount) {
        val cell = LIFNeuron()
        val electrode = Electrode(CurrentSignals(I_e = 2.0), rnd)
        electrode.connectToCell(cell)
        objects.add(cell)
    }

    val config = configure {
        addPathway(lifPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}

fun createLIFTrainingExperimentExcInhib() {
//    val logPath = "log/${System.currentTimeMillis()}/"
    val logPath = "log/excInhib/"
//    FileSaver.initModelWriters(logPath, setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))
    FileSaver.initModelWriters(logPath, setOf(SignalBooleanChangeResponse::class))

    val rnd: Random = Random(12345L)

    val n_exc = 100
    val n_inhib = n_exc

    val targetClasses = arrayListOf<String>("1")
    val provider = MnistProvider("C:\\projects\\mads\\mads_data\\MNIST_training\\", targetClasses)
    val electrodesArray = ElectrodeArray(provider, 25.0)

    val objects: ArrayList<ModelObject> = arrayListOf()

    objects.add(electrodesArray)

    val firstLayer: ArrayList<LIFNeuron> = arrayListOf()
    val secondLayer: ArrayList<LIFNeuron> = arrayListOf()
    val thirdLayer: ArrayList<LIFNeuron> = arrayListOf()

    val synapses: ArrayList<Synapse> = arrayListOf()
    val synapsesSecondToThird: ArrayList<Synapse> = arrayListOf()
    val synapsesThirdToSecond: ArrayList<Synapse> = arrayListOf()

    for(i in 0 until provider.width) {
        for(j in 0 until provider.height) {
            val cell = LIFNeuron()
            val electrode = electrodesArray.getElectrodeByCoordinate(i, j)
            electrode.connectToCell(cell)

            firstLayer.add(cell)
        }
    }

    for(i in 0 until n_inhib) {
        val cell = LIFNeuron()

        secondLayer.add(cell)
    }

    for(i in 0 until n_exc) {
        val cell = LIFNeuron()

        thirdLayer.add(cell)
    }

    for(i in 0 until firstLayer.size) {
        for(j in 0 until secondLayer.size) {
            val weight = 1.0 + Random.nextDouble() - 0.25
            val syn = connectCellsWithSynapse(firstLayer[i], secondLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
            synapses.add(syn)
        }
    }

    for(i in 0 until secondLayer.size) {
        val weight = 1.0 + Random.nextDouble() - 0.25
        val syn = connectCellsWithSynapse(secondLayer[i], thirdLayer[i], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
        synapsesSecondToThird.add(syn)
    }

    for(i in 0 until secondLayer.size) {
        for(j in 0 until thirdLayer.size) {
            if(i == j)
            {
                continue
            }

            val weight = 1.0 + Random.nextDouble() - 0.25
            val syn = connectCellsWithSynapse(thirdLayer[j], secondLayer[i], true, CurrentSignals(0.0), SynapseSignals(weight=weight))
            synapsesThirdToSecond.add(syn)
        }
    }


    objects.addAll(firstLayer)
    objects.addAll(secondLayer)
    objects.addAll(thirdLayer)
    objects.addAll(synapses)
    objects.addAll(synapsesSecondToThird)

    println(secondLayer.size)

    val config = configure {
        addPathway(synapsePathway())
        addPathway(electrodeArrayPathway())
        addPathway(lifPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()

    File("${logPath}/second_layer.txt").printWriter().use { out ->
        secondLayer.forEach {
            out.println("${it.hashCode()}")
        }
    }

    File("${logPath}/synapse1to2_weights.txt").printWriter().use { out ->
        synapses.forEach {
            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
            out.println("${w}")
        }
    }

    File("${logPath}/synapse2to3_weights.txt").printWriter().use { out ->
        synapsesSecondToThird.forEach {
            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
            out.println("${w}")
        }
    }

    File("${logPath}/synapse3to2_weights.txt").printWriter().use { out ->
        synapsesThirdToSecond.forEach {
            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
            out.println("${w}")
        }
    }
}