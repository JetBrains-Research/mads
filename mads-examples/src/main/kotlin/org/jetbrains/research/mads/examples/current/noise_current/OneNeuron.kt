package org.jetbrains.research.mads.examples.current.noise_current

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.Structure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.examples.Topology
import org.jetbrains.research.mads.examples.current.currentHHConfig
import org.jetbrains.research.mads.examples.current.currentIzhConfig
import org.jetbrains.research.mads.examples.current.currentLifConfig
import org.jetbrains.research.mads.examples.runExperiment
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads.ns.electrode.NoiseSignals
import org.jetbrains.research.mads.ns.physiology.neurons.*
import java.util.*

fun main() {
    val experimentName = "noise_current"
    val initialCurrent = 0.0
    val currentsMean = arrayOf(5.0, 10.0)
    val currentsSTD = arrayOf(0.5, 1.5, 2.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val randomSeed = 12345L
    val logSignals = listOf(
        SpikesSignals::spiked,
        PotentialSignals::V,
        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    currentLifConfig.addPathway(electrodeNoisePathway())
    currentIzhConfig.addPathway(electrodeNoisePathway())
    currentHHConfig.addPathway(electrodeNoisePathway())

    for (mean in currentsMean) {
        for (std in currentsSTD) {
            println("Experiments with $std nA current")
            runExperiment(
                logFolder = "$experimentName/${mean}+${std}_microA/lif/${startTime}",
                logSignals = logSignals,
                logTypes = listOf(),
                topology = Structure(Topology.electrodeNeuron(
                    { -> Electrode(Random(randomSeed), CurrentSignals(I_e = initialCurrent), NoiseSignals(mean, std)) },
                    { -> LIFNeuron(LIFConstants.V_thresh) }
                )),
                config = currentLifConfig,
                stopCondition = Model.timeStopCondition((modelingTime.toBigDecimal() / currentLifConfig.timeResolution.toBigDecimal()).toLong())
            )
            runExperiment(
                logFolder = "$experimentName/${mean}+${std}_microA/izh/${startTime}",
                logSignals = logSignals,
                logTypes = listOf(),
                topology = Structure(Topology.electrodeNeuron(
                    { -> Electrode(Random(randomSeed), CurrentSignals(I_e = initialCurrent), NoiseSignals(mean, std)) },
                    { -> IzhNeuron() }
                )),
                config = currentIzhConfig,
                stopCondition = Model.timeStopCondition((modelingTime.toBigDecimal() / currentIzhConfig.timeResolution.toBigDecimal()).toLong())
            )
            runExperiment(
                logFolder = "$experimentName/${mean}+${std}_microA/hh/${startTime}",
                logSignals = logSignals,
                logTypes = listOf(),
                topology = Structure(Topology.electrodeNeuron(
                    { -> Electrode(Random(randomSeed), CurrentSignals(I_e = initialCurrent), NoiseSignals(mean, std)) },
                    { -> HHNeuron(HHConstants.V_thresh, HHSignals()) }
                )),
                config = currentHHConfig,
                stopCondition = Model.timeStopCondition((modelingTime.toBigDecimal() / currentHHConfig.timeResolution.toBigDecimal()).toLong())
            )
        }
    }
}

fun electrodeNoisePathway() = pathway {
    timeResolution = microsecond
    mechanism(mechanism = ElectrodeMechanisms.NoiseDynamic) {
        duration = 500
        condition = Always
    }
}
