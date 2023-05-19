package org.jetbrains.research.mads.experiments.const_current

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.connectCellsWithSynapse
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.pathways.*
import org.jetbrains.research.mads.ns.physiology.neurons.*
import java.util.*
import kotlin.io.path.Path

fun main() {
    val currents = arrayOf<Double>(10.0, 20.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val randomSeed = 12345L
    println("Experiment start time $startTime")

    for (current in currents) {
        println("Experiments with $current nA current")
        experimentWithTwoNeurons(current, "lif/${startTime}",
            { -> LIFNeuron(LIFConstants.V_thresh) },
            configure {
                timeResolution = microsecond
                addPathway(lifPathway())
                addPathway(synapsePathway())
            },
            modelingTime, randomSeed
        )
        experimentWithTwoNeurons(current, "izh/${startTime}",
            { -> IzhNeuron() },
            configure {
                timeResolution = microsecond
                addPathway(izhPathway())
                addPathway(synapsePathway())
            },
            modelingTime, randomSeed
        )
        experimentWithTwoNeurons(current, "hh/${startTime}",
            { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
            configure {
                timeResolution = microsecond
                addPathway(hhPathway())
                addPathway(synapsePathway())
            },
            modelingTime, randomSeed
        )
    }
}

fun experimentWithTwoNeurons(current: Double, logFolder: String, neuronFun: () -> Neuron, config: Configuration, time: Double, seed: Long) {
    val dir = Path("log/const_current/TwoNeurons/${current}_nA/${logFolder}")
    val saver = FileSaver(dir)
    saver.addSignalsNames(SpikesSignals::spiked)    // here we have boolean true for spike occurrence
//    saver.addSignalsNames(PotentialSignals::V)      // here is membrane potential (both cells)
//    saver.addSignalsNames(CurrentSignals::I_e)      // here is current (electrode and synapse)

    val rnd = Random(seed)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1

    for (i in 0 until neuronCount) {
        val cellF = neuronFun()
        val cellS = neuronFun()
        val electrode = Electrode(rnd, CurrentSignals(I_e = current))
        electrode.connectToCell(cellF)
        val synapse = connectCellsWithSynapse(cellF, cellS, false)
        cellF.type = "first_neuron"
        cellS.type = "second_neuron"
        electrode.type = "electrode"
        synapse.type = "synapse"
        objects.add(cellF)
        objects.add(cellS)
        objects.add(synapse)
        objects.add(electrode)
    }

    val s = Model(objects, config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}