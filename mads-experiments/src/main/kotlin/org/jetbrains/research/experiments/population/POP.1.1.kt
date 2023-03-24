package org.jetbrains.research.experiments.population

import org.jetbrains.research.experiments.connectElectrodes
import org.jetbrains.research.experiments.connectPopulations
import org.jetbrains.research.experiments.createPopulation
import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads_ns.electrode.NoiseSignals
import org.jetbrains.research.mads_ns.physiology.neurons.*
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import java.util.*
import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.reflect.KProperty

enum class Connectedness {
    NONE,
    RANDOM,
    CONSTANT
}

fun main() {
    pop_13_3()
//    val experimentName = "POP.13"
//    val timePart = System.currentTimeMillis().toString()
//    val noiseStds = arrayOf(2.0, 5.0, 7.0, 10.0)
//    noiseStds.forEach { noiseEXC ->
//        noiseStds.forEach { noiseINH ->
//            run(noiseEXC, noiseINH, "$experimentName/$timePart", true)
//        }
//    }
}

fun pop_3_1(
    experimentName:String="POP_3.1"
) {
    val timePart = System.currentTimeMillis().toString()
    runSimulation(5.0,2.0,8,2,0.5,-1.0,Connectedness.RANDOM, "$experimentName/$timePart")
}

fun pop_3_2(
    experimentName:String="POP_3.2"
) {
    val timePart = System.currentTimeMillis().toString()
    runSimulation(5.0,2.0,75,25,0.5,-1.0,Connectedness.RANDOM, "$experimentName/$timePart")
    runSimulation(5.0,2.0,80,20,0.5,-1.0,Connectedness.RANDOM, "$experimentName/$timePart")
    runSimulation(5.0,2.0,85,25,0.5,-1.0,Connectedness.RANDOM, "$experimentName/$timePart")
}

fun pop_13_1(
    experimentName:String="POP_13.1"
) {
    val timePart = System.currentTimeMillis().toString()
    runSimulation(5.0,2.0,8,2,0.5,-1.0,Connectedness.CONSTANT, "$experimentName/$timePart")
}

fun pop_13_2(
    experimentName:String="POP_13.2"
) {
    val timePart = System.currentTimeMillis().toString()
    arrayOf(0.1,0.15,0.2,0.25,0.3).forEach {
        runSimulation(5.0,2.0,8,2,it,-1.0,Connectedness.CONSTANT, "$experimentName/$timePart")
    }
}

fun pop_13_3(
    experimentName:String="POP_13.3"
) {
    val timePart = System.currentTimeMillis().toString()
    runSimulation(10.0,5.0,8,2,0.5,-1.0,Connectedness.CONSTANT, "$experimentName/$timePart")
}




fun runSimulation(
    noiseExc:Double,            // noise std of excitatory neurons
    noiseInh:Double,            // noise std of inhibitory neurons
    nExc:Int,                   // number of excitatory neurons
    nInh:Int,                   // number of inhibitory neurons
    synWeightExc:Double=0.5,    // synapse weight excitatory synapses
    synWeightInh:Double=-1.0,   // synapse weight inhibitory synapses
    connected:Connectedness,    // connection mode
    logPrefix:String
) {
    val startTime = System.currentTimeMillis()
    val modelingTime = 5000 * millisecond
    val randomSeed = 12345L
    val logSignals = arrayListOf<KProperty<*>>(
        SpikesSignals::spiked,
//        PotentialSignals::V,
//        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    val dir = Path("log/${logPrefix}/izh/${noiseExc}-${noiseInh}/${nExc}-${nInh}/${synWeightExc}-${synWeightInh}/${connected}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }

    val rE = Random(randomSeed)
    val rI = Random(randomSeed - 1)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val eNeurons: List<Neuron> = createPopulation(nExc, "excitatory") { ->
        IzhNeuron(
            IzhConstants(
            a = 0.02,
            b = 0.2,
            c = -65.0 + 15 * rE.nextDouble().pow(2),
            d = 8.0 - 6.0 * rE.nextDouble().pow(2))
        )
    }
    val iNeurons: List<Neuron> = createPopulation(nInh, "inhibitory") { ->
        IzhNeuron(
            IzhConstants(
            a = 0.02 + 0.08 * rI.nextDouble(),
            b = 0.25 - 0.05 * rI.nextDouble(),
            c = -65.0,
            d = 2.0)
        )
    }
    objects.addAll(eNeurons)
    objects.addAll(iNeurons)

    val eElectrodes = connectElectrodes(eNeurons) { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = noiseExc)) }
    val iElectrodes = connectElectrodes(iNeurons) { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = noiseInh)) }

    objects.addAll(eElectrodes)
    objects.addAll(iElectrodes)

    if (connected != Connectedness.NONE) {
        val synapses: ArrayList<Synapse> = arrayListOf()
        val randomExcComponent = if (connected==Connectedness.RANDOM) rE.nextDouble() else 1.0
        val randomInhComponent = if (connected==Connectedness.RANDOM) rE.nextDouble() else 1.0
        synapses.addAll(connectPopulations(eNeurons, eNeurons, weight = { synWeightExc * randomExcComponent }))
        synapses.addAll(connectPopulations(eNeurons, iNeurons, weight = { synWeightExc * randomInhComponent }))
        synapses.addAll(connectPopulations(iNeurons, eNeurons, weight = { synWeightInh * rI.nextDouble() }))
        synapses.addAll(connectPopulations(iNeurons, iNeurons, weight = { synWeightInh * rI.nextDouble() }))
        objects.addAll(synapses)
    }

    val config = configure {
        timeResolution = microsecond
        addPathway(customIzhPathway())
        addPathway(electrodeNoisePathway())
    }

    val s = Model(objects, config)
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}

fun electrodeNoisePathway() = pathway {
    timeResolution = microsecond
    mechanism(mechanism = ElectrodeMechanisms.NoiseDynamic) {
        duration = 1000
        condition = Always
    }
}