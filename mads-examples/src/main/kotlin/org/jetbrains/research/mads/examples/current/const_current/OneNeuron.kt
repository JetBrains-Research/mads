package org.jetbrains.research.mads.examples.current.const_current

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.examples.Topology.Companion.constCurrent
import org.jetbrains.research.mads.examples.current.currentHHConfig
import org.jetbrains.research.mads.examples.current.currentIzhConfig
import org.jetbrains.research.mads.examples.current.currentLifConfig
import org.jetbrains.research.mads.examples.runExperiment
import org.jetbrains.research.mads.ns.physiology.neurons.*

fun main() {
    val experimentName = "const_current"
    val currents = arrayOf(5.0, 10.0, 20.0, 30.0, 50.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val logSignals = listOf(
        SpikesSignals::spiked,
        PotentialSignals::V,
        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    for (current in currents) {
        println("Experiments with $current nA current")
        runExperiment(
            logFolder = "$experimentName/${current}_microA/lif/${startTime}",
            logSignals = logSignals,
            logTypes = listOf(),
            topology = constCurrent(
                { -> LIFNeuron(LIFConstants.V_thresh) },
                current
            ),
            config = currentLifConfig,
            stopCondition = Model.timeStopCondition((modelingTime.toBigDecimal() / currentLifConfig.timeResolution.toBigDecimal()).toLong())
        )
        runExperiment(
            logFolder = "$experimentName/${current}_microA/izh/${startTime}",
            logSignals = logSignals,
            logTypes = listOf(),
            topology = constCurrent(
                { -> IzhNeuron() },
                current
            ),
            config = currentIzhConfig,
            stopCondition = Model.timeStopCondition((modelingTime.toBigDecimal() / currentIzhConfig.timeResolution.toBigDecimal()).toLong())
        )
        runExperiment(
            logFolder = "$experimentName/${current}_microA/hh/${startTime}",
            logSignals = logSignals,
            logTypes = listOf(),
            topology = constCurrent(
                { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
                current
            ),
            config = currentHHConfig,
            stopCondition = Model.timeStopCondition((modelingTime.toBigDecimal() / currentHHConfig.timeResolution.toBigDecimal()).toLong())
        )
    }
}