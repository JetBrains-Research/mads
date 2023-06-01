package org.jetbrains.research.mads.examples.training.lif

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads.examples.training.Topology
import org.jetbrains.research.mads.examples.training.runExperiment
import org.jetbrains.research.mads.ns.physiology.neurons.AdaptiveLIFNeuron
import org.jetbrains.research.mads.ns.physiology.neurons.SpikesSignals
import org.jetbrains.research.mads.ns.physiology.neurons.TimerInputNeuron
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals

fun main() {
    basicLifSynapse()
//    basicLifSynapseInhib()
}

fun basicLifSynapse() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 1 * second
    val config = lifBasicInput()
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()

    val logSignals = listOf(
//        SpikesSignals::spiked,
        SynapseSignals::weight,
//        PotentialSignals::V,
//        CurrentSignals::I_e,
//        AdaptiveSignals::theta,
//        AdaptiveSignals::icond,
//        STDPTripletSignals::stdpTracePre,
//        STDPTripletSignals::stdpTracePost1,
//        STDPTripletSignals::stdpTracePost2,
//        AdaptiveSignals::theta
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
        objects,
        lifBasicInput(),
        Model.timeStopCondition(stopTime)
    )
}

fun basicLifSynapseInhib() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 1 * second
    val config = lifBasicInput()
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()

    val logSignals = listOf(
        SpikesSignals::spiked,
//        SynapseSignals::weight,
//        SynapseSignals::releaserSpiked,
//        PotentialSignals::V,
//        CurrentSignals::I_e,
//        AdaptiveSignals::theta,
//        AdaptiveSignals::icond,
//        STDPTripletSignals::stdpTracePre,
//        STDPTripletSignals::stdpTracePost1,
//        STDPTripletSignals::stdpTracePost2,
//        AdaptiveSignals::theta
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
        objects,
        lifBasicInput(),
        Model.timeStopCondition(stopTime)
    )
}