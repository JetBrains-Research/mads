package org.jetbrains.research.mads.experiments.pulse_current

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.PeriodicPulsationSignals
import org.jetbrains.research.mads.ns.pathways.*
import org.jetbrains.research.mads.ns.physiology.neurons.*
import java.util.*
import kotlin.io.path.Path

fun main() {
//    val currents = arrayOf<Double>(10.0)
    val currents = arrayOf<Double>(5.0, 10.0)
    val periodic = arrayListOf<Int>(5, 10, 15)
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
        for (period in periodic) {
            println("Experiments with $current nA current")
            experimentWithCurrents(
                current, period, "lif/${startTime}",
                { -> LIFNeuron(LIFConstants.V_thresh) },
                configure {
                    timeResolution = microsecond
                    addPathway(lifPathway())
                    addPathway(electrodePeriodicPulsePathway())
                },
                modelingTime, randomSeed
            )
            experimentWithCurrents(
                current, period, "izh/${startTime}",
                { -> IzhNeuron() },
                configure {
                    timeResolution = microsecond
                    addPathway(izhPathway())
                    addPathway(electrodePeriodicPulsePathway())
                },
                modelingTime, randomSeed
            )
            experimentWithCurrents(
                current, period, "hh/${startTime}",
                { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
                configure {
                    timeResolution = microsecond
                    addPathway(hhPathway())
                    addPathway(electrodePeriodicPulsePathway())
                },
                modelingTime, randomSeed
            )
        }
    }
}

fun experimentWithCurrents(current: Double, period: Int, logFolder: String, neuronFun: () -> Neuron, config: Configuration, time: Double, seed: Long) {
    val dir = Path("log/periodic_pulse_current/OneNeuron/${current}_nA/${period}/${logFolder}")
    val saver = FileSaver(dir)
    saver.addSignalsNames(SpikesSignals::spiked)
//    saver.addSignalsNames(CurrentSignals::I_e)

    val rnd = Random(seed)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1

    for (i in 0 until neuronCount) {
        val cell = neuronFun()

        // we are starting from 0 mA
        val electrode = Electrode(rnd, CurrentSignals(I_e = 0.0), PeriodicPulsationSignals(cycleCounter = period, pulseValue = current))
        cell.type = "neuron"
        electrode.type = "electrode"
        electrode.connectToCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val s = Model(objects, config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}