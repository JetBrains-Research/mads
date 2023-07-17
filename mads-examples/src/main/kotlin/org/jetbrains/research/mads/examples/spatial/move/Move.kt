package org.jetbrains.research.mads.examples.spatial.move

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.SpatialSignals
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads.examples.runExperiment

fun main() {
    move()
}

fun move() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 100 * second
    val config = moveConfig
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    println("Experiment start time $startTime")

    runExperiment(
        logFolder = "move/${startTime}",
        mapOf(
//            "spread" to hashSetOf(DiffusibleSignals::A),
//            "gather" to hashSetOf(DiffusibleSignals::A, SpatialSignals::coordinate)
            "gather" to hashSetOf(SpatialSignals::coordinate),
//            "space" to hashSetOf(DiffuseSignals::log)
        ),
        moveStructure,
        moveConfig,
        Model.timeStopCondition(stopTime)
    )
}
