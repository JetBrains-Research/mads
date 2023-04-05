package org.jetbrains.research.experiments.noise_current

import org.jetbrains.research.experiments.experimentWithElectrodeAndNeuron
import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads.ns.electrode.NoiseConstants
import org.jetbrains.research.mads.ns.pathways.hhPathway
import org.jetbrains.research.mads.ns.pathways.izhPathway
import org.jetbrains.research.mads.ns.pathways.lifPathway
import org.jetbrains.research.mads.ns.physiology.neurons.*
import kotlin.reflect.KProperty

fun main() {
    val experimentName = "noise_current"
    val initialCurrent = 0.0
    val currentsMean = arrayOf(5.0, 10.0)
    val currentsSTD = arrayOf(0.5, 1.5, 2.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 200 * millisecond
    val randomSeed = 12345L
    val logSignals = arrayListOf<KProperty<*>>(
        SpikesSignals::spiked,
        PotentialSignals::V,
        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    for (mean in currentsMean) {
        for (std in currentsSTD) {
            println("Experiments with $std nA current")
            experimentWithElectrodeAndNeuron(
                experimentName,"${mean}+${std}_microA/lif/${startTime}",
                logSignals, initialCurrent,
                { -> LIFNeuron(LIFConstants.V_thresh) },
                configure {
                    timeResolution = microsecond
                    addPathway(lifPathway())
                    addPathway(electrodeNoisePathway(mean, std))
                },
                modelingTime, randomSeed
            )
            experimentWithElectrodeAndNeuron(
                experimentName,"${mean}+${std}_microA/izh/${startTime}",
                logSignals, initialCurrent,
                { -> IzhNeuron() },
                configure {
                    timeResolution = microsecond
                    addPathway(izhPathway())
                    addPathway(electrodeNoisePathway(mean, std))
                },
                modelingTime, randomSeed
            )
            experimentWithElectrodeAndNeuron(
                experimentName,"${mean}+${std}_microA/hh/${startTime}",
                logSignals, initialCurrent,
                { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
                configure {
                    timeResolution = microsecond
                    addPathway(hhPathway())
                    addPathway(electrodeNoisePathway(mean, std))
                },
                modelingTime, randomSeed
            )
        }
    }
}

fun electrodeNoisePathway(mean: Double, std: Double) = pathway {
    timeResolution = microsecond
    mechanism(mechanism = ElectrodeMechanisms.NoiseDynamic) {
        duration = 500
        condition = Always
        constants = NoiseConstants(meanValue = mean, std = std)
    }
}
