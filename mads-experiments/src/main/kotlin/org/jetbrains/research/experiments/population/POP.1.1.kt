package org.jetbrains.research.experiments.population

import org.jetbrains.research.experiments.connectElectrodes
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
import java.util.*
import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.reflect.KProperty

fun main() {
    val experimentName = "POP.2"
    val timePart = System.currentTimeMillis().toString()
    val noiseStds = arrayOf(2.0, 5.0, 7.0, 10.0)
    noiseStds.forEach { noiseEXC ->
        noiseStds.forEach { noiseINH ->
            run(noiseEXC, noiseINH, "$experimentName/$timePart")
        }
    }
}

fun run(noiseEXC:Double, noiseINH:Double, logPrefix:String) {
    val startTime = System.currentTimeMillis()
    val modelingTime = 5000 * millisecond
    val randomSeed = 12345L
    val logSignals = arrayListOf<KProperty<*>>(
        SpikesSignals::spiked,
//        PotentialSignals::V,
//        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    val dir = Path("log/${logPrefix}/izh/${noiseEXC}/${noiseINH}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }

    val nExc = 10   // count of excitatory neurons
    val nInh = 10    // count of inhibitory neurons

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
            a = 0.02 + 0.08 * rI.nextDouble().pow(2),
            b = 0.25 - 0.05 * rI.nextDouble().pow(2),
            c = -65.0,
            d = 2.0)
        )
    }
    objects.addAll(eNeurons)
    objects.addAll(iNeurons)


    val eElectrodes = connectElectrodes(eNeurons) { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = noiseEXC)) }
    val iElectrodes = connectElectrodes(iNeurons) { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = noiseINH)) }

    objects.addAll(eElectrodes)
    objects.addAll(iElectrodes)

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