package org.jetbrains.research.experiments.const_current

import org.jetbrains.research.mads.core.configuration.Configuration
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
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
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
    var currents = arrayOf<Double>(5.0, 10.0, 20.0, 30.0, 50.0)
//    var currents = arrayOf<Double>(-10.0, -5.0, 0.0, 5.0, 10.0)
    var startTime = System.currentTimeMillis()
    var ticks = 10_000L
    var randomSeed = 12345L
    println("Experiment start time $startTime")

    for (current in currents) {
        println("Experiments with $current nA current")
        experimentWithCurrents(current, "lif/${startTime}",
            { -> LIFNeuron(LIFConstants.V_thresh) },
            configure { addPathway(lifPathway())},
            ticks, randomSeed
        )
        experimentWithCurrents(current, "izh/${startTime}",
            { -> IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals()) },
            configure { addPathway(izhPathway()) },
            ticks, randomSeed
        )
        experimentWithCurrents(current, "hh/${startTime}",
            { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
            configure { addPathway(hhPathway()) },
            ticks, randomSeed
        )
    }
}

fun experimentWithCurrents(current: Double, logFolder: String, neuronFun: () -> Neuron, config: Configuration, ticks: Long, seed: Long) {
    FileSaver.initModelWriters("log/const_current/${current}_nA/${logFolder}/")
    val rnd = Random(seed)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1

    for (i in 0 until neuronCount) {
        val cell = neuronFun()
        val electrode = Electrode(CurrentSignals(I_e = current), rnd)
        cell.type = "neuron"
        electrode.type = "electrode"
        electrode.connectToCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > ticks }
    FileSaver.closeModelWriters()
}