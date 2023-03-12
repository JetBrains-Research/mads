package org.jetbrains.research.experiments.const_current

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.telemetry.JsonModelExporter
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.pathways.connectToCell
import org.jetbrains.research.mads_ns.pathways.hhPathway
import org.jetbrains.research.mads_ns.pathways.izhPathway
import org.jetbrains.research.mads_ns.pathways.lifPathway
import org.jetbrains.research.mads_ns.physiology.neurons.*
import kotlin.io.path.Path
import kotlin.random.Random

fun main() {
    val currents = arrayOf<Double>(10.0)
//    val currents = arrayOf<Double>(5.0, 10.0, 20.0, 30.0, 50.0)
//    var currents = arrayOf<Double>(-10.0, -5.0, 0.0, 5.0, 10.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val randomSeed = 12345L
    println("Experiment start time $startTime")

//    for (i in 0..100) {
//        experimentWithCurrents(currents[0], "hh/${System.currentTimeMillis()}",
//            { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
//            configure {
//                timeResolution = microsecond
//                addPathway(hhPathway())
//            },
//            modelingTime, randomSeed
//        )
//    }

    for (current in currents) {
        println("Experiments with $current nA current")
        experimentWithCurrents(current, "lif/${startTime}",
            { -> LIFNeuron(LIFConstants.V_thresh) },
            configure {
                timeResolution = microsecond
                addPathway(lifPathway())
            },
            modelingTime, randomSeed
        )
        experimentWithCurrents(current, "izh/${startTime}",
            { -> IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals()) },
            configure {
                timeResolution = microsecond
                addPathway(izhPathway())
            },
            modelingTime, randomSeed
        )
        experimentWithCurrents(current, "hh/${startTime}",
            { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
            configure {
                timeResolution = microsecond
                addPathway(hhPathway())
            },
            modelingTime, randomSeed
        )
    }
}

fun experimentWithCurrents(current: Double, logFolder: String, neuronFun: () -> Neuron, config: Configuration, time: Double, seed: Long) {
    val saver = FileSaver("log/const_current/${current}_nA/${logFolder}/")
    saver.addSignalsNames(SpikesSignals::spiked)
    saver.addSignalsNames(PotentialSignals::V)

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

    val path = Path("log/const_current/OneNeuron/${current}_nA/${logFolder}/states.json")
    val modelExporter: JsonModelExporter = JsonModelExporter(path)
    val s = Model(objects, config)
    s?.let { modelExporter.write(it) }
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    s?.let { modelExporter.write(it) }
    modelExporter.close()
    saver.closeModelWriters()
}