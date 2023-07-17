package org.jetbrains.research.mads.examples.circuits.lif

import org.jetbrains.research.mads.core.configuration.Structure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads.examples.Topology
import org.jetbrains.research.mads.examples.runExperiment
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals

fun main() {
    twoNeuronsCircuit()
    threeNeuronsCircuit()
}

fun twoNeuronsCircuit() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 5 * second
    val config = simpleCircuitConfigLif
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()

    val logSignals = listOf(
        SpikesSignals::spiked,
        SynapseSignals::weight,
        PotentialSignals::V,
        CurrentSignals::I_e,
        AdaptiveSignals::theta,
        AdaptiveSignals::icond,
        STDPTripletSignals::stdpTracePre,
        STDPTripletSignals::stdpTracePost1,
        STDPTripletSignals::stdpTracePost2,
        AdaptiveSignals::theta
    )

    val objects = Topology.excitatorySimple(
        { -> TimerInputNeuron() },
        { -> AdaptiveLIFNeuron(adaptiveThreshold = true) }
    )

    println("Experiment start time $startTime")
    runExperiment(
        "lif/basic_syn/$startTime",
        logSignals = logSignals,
        listOf(),
        Structure(objects),
        config,
        Model.timeStopCondition(stopTime)
    )
}

fun threeNeuronsCircuit() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 5 * second
    val config = simpleCircuitConfigLif
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()

    val logSignals = listOf(
        SpikesSignals::spiked,
        SynapseSignals::weight,
        SynapseSignals::releaserSpiked,
        PotentialSignals::V,
        CurrentSignals::I_e,
        AdaptiveSignals::theta,
        AdaptiveSignals::icond,
        STDPTripletSignals::stdpTracePre,
        STDPTripletSignals::stdpTracePost1,
        STDPTripletSignals::stdpTracePost2,
        AdaptiveSignals::theta
    )

    val objects = Topology.inhibitorySimple(
        { -> TimerInputNeuron() },
        { -> AdaptiveLIFNeuron(adaptiveThreshold = true) },
        { -> AdaptiveLIFNeuron(adaptiveThreshold = false, isInhibitory = true) }
    )

    println("Experiment start time $startTime")
    runExperiment(
        "lif/basic_inh/$startTime",
        logSignals = logSignals,
        listOf(),
        Structure(objects),
        config,
        Model.timeStopCondition(stopTime)
    )
}