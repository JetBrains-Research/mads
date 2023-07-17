package org.jetbrains.research.mads.examples.population

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Structure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads.ns.connectElectrodes
import org.jetbrains.research.mads.ns.connectPopulations
import org.jetbrains.research.mads.ns.createPopulation
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.NoiseSignals
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import java.util.*
import kotlin.io.path.Path
import kotlin.math.pow

enum class Connectedness {
    NONE,
    RANDOM,
    CONSTANT
}

fun main() {
    val experimentName = "izh_population_1k"
    val timePart = System.currentTimeMillis().toString()
    val modelingTime = 1 * second
    runSimulation(
        time = modelingTime,
        noiseExc = 5.0,
        noiseInh = 2.0,
        nExc = 790,
        nInh = 210,
        synWeightExc = 0.5,
        synWeightInh = -1.0,
        connected = Connectedness.RANDOM,
        logPrefix = "$experimentName/$timePart",
        config = config
    )
}

fun runSimulation(
    time: Double,                   // modeling time
    noiseExc: Double,               // noise std of excitatory neurons
    noiseInh: Double,               // noise std of inhibitory neurons
    nExc: Int,                      // number of excitatory neurons
    nInh: Int,                      // number of inhibitory neurons
    synWeightExc: Double = 0.5,     // synapse weight excitatory synapses
    synWeightInh: Double = -1.0,    // synapse weight inhibitory synapses
    connected: Connectedness,       // connection mode
    logPrefix: String,              // log prefix
    config: Configuration           // configuration
) {
    val startTime = System.currentTimeMillis()
    val randomSeed = 12345L
    val logSignals = listOf(
        SpikesSignals::spiked,
    )
    println("Experiment start time $startTime")

    val dir = Path(
        "log/${logPrefix}/izh/${noiseExc}-${noiseInh}/${nExc}-${nInh}" +
                "/${synWeightExc}-${synWeightInh}/${connected}"
    )
    val saver = FileSaver(dir)
    val r = Random(randomSeed)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val eNeurons: List<Neuron> = createPopulation(nExc, "excitatory") { ->
        IzhNeuron(
            IzhConstants(
                a = 0.02,
                b = 0.2,
                c = -65.0 + 15 * r.nextDouble().pow(2),
                d = 8.0 - 6.0 * r.nextDouble().pow(2)
            )
        )
    }
    val iNeurons: List<Neuron> = createPopulation(nInh, "inhibitory") { ->
        IzhNeuron(
            IzhConstants(
                a = 0.02 + 0.08 * r.nextDouble(),
                b = 0.25 - 0.05 * r.nextDouble(),
                c = -65.0,
                d = 2.0
            )
        )
    }
    objects.addAll(eNeurons)
    objects.addAll(iNeurons)

    val eElectrodes = connectElectrodes(eNeurons)
    { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = noiseExc)) }
    val iElectrodes = connectElectrodes(iNeurons)
    { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = noiseInh)) }

    objects.addAll(eElectrodes)
    objects.addAll(iElectrodes)

    if (connected != Connectedness.NONE) {
        val synapses: ArrayList<Synapse> = arrayListOf()
        synapses.addAll(
            connectPopulations(eNeurons, eNeurons,
                weight = { synWeightExc * if (connected == Connectedness.RANDOM) r.nextDouble() else 1.0 })
        )
        synapses.addAll(
            connectPopulations(eNeurons, iNeurons,
                weight = { synWeightExc * if (connected == Connectedness.RANDOM) r.nextDouble() else 1.0 })
        )
        synapses.addAll(
            connectPopulations(iNeurons, eNeurons,
                weight = { synWeightInh * if (connected == Connectedness.RANDOM) r.nextDouble() else 1.0 })
        )
        synapses.addAll(
            connectPopulations(iNeurons, iNeurons,
                weight = { synWeightInh * if (connected == Connectedness.RANDOM) r.nextDouble() else 1.0 })
        )
        objects.addAll(synapses)
    }

    val allTypes = objects.distinctBy { it.type }.map { it.type }
    allTypes.forEach { type ->
        logSignals.forEach { signal ->
            saver.addObjTypeSignalFilter(type, signal)
        }
    }

    val s = Model(Structure(objects), config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.nextTime() > stopTime }
    saver.closeModelWriters()
}