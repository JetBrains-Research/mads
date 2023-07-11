package org.jetbrains.research.mads.examples.spatial

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads.examples.runExperiment

fun main() {
    diffuse()
}

fun diffuse() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 100 * second
    val config = diffuseConfig
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    println("Experiment start time $startTime")

    runExperiment(
        logFolder = "diffuse/${startTime}",
        mapOf(
            "spread" to hashSetOf(DiffusibleSignals::A),
            "gather" to hashSetOf(DiffusibleSignals::A)
        ),
        diffuseStructure,
        diffuseConfig,
        Model.timeStopCondition(stopTime)
    )
}
