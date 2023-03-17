package org.jetbrains.research.experiments.const_current

import org.jetbrains.research.experiments.experimentWithElectrodeAndNeuron
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.pathways.hhPathway
import org.jetbrains.research.mads_ns.pathways.izhPathway
import org.jetbrains.research.mads_ns.pathways.lifPathway
import org.jetbrains.research.mads_ns.physiology.neurons.*
import kotlin.reflect.KProperty

fun main() {
    val experimentName = "const_current"
    val currents = arrayOf(5.0, 10.0, 20.0, 30.0, 50.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val randomSeed = 12345L
    val logSignals = arrayListOf<KProperty<*>>(
        SpikesSignals::spiked,
        PotentialSignals::V,
        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    for (current in currents) {
        println("Experiments with $current nA current")
        experimentWithElectrodeAndNeuron(
            experimentName,"${current}_microA/lif/${startTime}",
            logSignals, current,
            { -> LIFNeuron(LIFConstants.V_thresh) },
            configure {
                timeResolution = microsecond
                addPathway(lifPathway())
            },
            modelingTime, randomSeed
        )
        experimentWithElectrodeAndNeuron(
            experimentName,"${current}_microA/lif/${startTime}",
            logSignals, current,
            { -> IzhNeuron() },
            configure {
                timeResolution = microsecond
                addPathway(izhPathway())
            },
            modelingTime, randomSeed
        )
        experimentWithElectrodeAndNeuron(
            experimentName,"${current}_microA/lif/${startTime}",
            logSignals, current,
            { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
            configure {
                timeResolution = microsecond
                addPathway(hhPathway())
            },
            modelingTime, randomSeed
        )
    }
}