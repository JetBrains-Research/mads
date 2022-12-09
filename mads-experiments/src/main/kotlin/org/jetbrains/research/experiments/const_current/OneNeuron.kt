package org.jetbrains.research.experiments.const_current

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.pathways.connectToCell
import org.jetbrains.research.mads_ns.pathways.hhPathway
import org.jetbrains.research.mads_ns.pathways.izhPathway
import org.jetbrains.research.mads_ns.pathways.lifPathway
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHConstants
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHNeuron
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHSignals
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhConstantsRS
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhNeuron
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhSignals
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFConstants
import org.jetbrains.research.mads_ns.physiology.neurons.lif.LIFNeuron
import kotlin.random.Random

fun main() {
    var current = 0.0

    // 1st set, current = 5.0 nA
    current = 5.0
    println("Experiments with $current nA current")
    LIFConstCurrentExperiment(current)
    IzhConstCurrentExperiment(current)
    HHConstCurrentExperiment(current)

    // 1st set, current = 10.0 nA
    current = 10.0
    println("Experiments with $current nA current")
    LIFConstCurrentExperiment(current)
    IzhConstCurrentExperiment(current)
    HHConstCurrentExperiment(current)

    // 1st set, current = 20.0 nA
    current = 20.0
    println("Experiments with $current nA current")
    LIFConstCurrentExperiment(current)
    IzhConstCurrentExperiment(current)
    HHConstCurrentExperiment(current)

    // 1st set, current = 30.0 nA
    current = 30.0
    println("Experiments with $current nA current")
    LIFConstCurrentExperiment(current)
    IzhConstCurrentExperiment(current)
    HHConstCurrentExperiment(current)

//    // 1st set, current = 50.0 nA
    current = 50.0
    println("Experiments with $current nA current")
    LIFConstCurrentExperiment(current)
    IzhConstCurrentExperiment(current)
    HHConstCurrentExperiment(current)
}

fun LIFConstCurrentExperiment(current: Double) {
    FileSaver.initModelWriters("log/const_current/${current}_nA/lif/${System.currentTimeMillis()}/")
    val rnd = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1

    for (i in 0 until neuronCount) {
        val cell = LIFNeuron(LIFConstants.V_thresh)
        val electrode = Electrode(CurrentSignals(I_e = current), rnd)
        cell.type = "neuron"
        electrode.type = "electrode"
        electrode.connectToCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val config = configure {
        addPathway(lifPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}

fun IzhConstCurrentExperiment(current: Double) {
    FileSaver.initModelWriters("log/const_current/${current}_nA/izh/${System.currentTimeMillis()}/")
    val rnd = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1
    for (i in 0 until neuronCount) {
        val cell = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())
        val electrode = Electrode(CurrentSignals(I_e = current), rnd)
        cell.type = "neuron"
        electrode.type = "electrode"
        electrode.connectToCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val config = configure {
        addPathway(izhPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}

fun HHConstCurrentExperiment(current: Double) {
    FileSaver.initModelWriters("log/const_current/${current}_nA/hh/${System.currentTimeMillis()}/")
    val rnd = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1
    for (i in 0 until neuronCount) {
        val cell = HHNeuron(HHConstants.V_thresh, HHSignals())
        val electrode = Electrode(CurrentSignals(I_e = current), rnd)
        cell.type = "neuron"
        electrode.type = "electrode"
        electrode.connectToCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val config = configure {
        addPathway(hhPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}