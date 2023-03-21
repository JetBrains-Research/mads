package org.jetbrains.research.experiments.population

import org.jetbrains.research.experiments.connectCellsWithSynapse
import org.jetbrains.research.experiments.createPopulation
import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.pathways.overThresholdAndNotSpiked
import org.jetbrains.research.mads_ns.pathways.underThresholdAndSpiked
import org.jetbrains.research.mads_ns.physiology.neurons.*
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseSignals
import java.util.*
import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.reflect.KProperty

fun main() {
    val experimentName = "population"
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val randomSeed = 12345L
    val logSignals = arrayListOf<KProperty<*>>(
        SpikesSignals::spiked,
//        PotentialSignals::V,
//        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    val dir = Path("log/" + experimentName + "/izh/${startTime}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }

    val nExc = 800 //00
    val nInh = 200 //00

    val rE = Random(randomSeed)
    val rI = Random(randomSeed - 1)

    val eNeurons: List<Neuron> = createPopulation(nExc, "excitatory") { ->
        IzhNeuron(IzhConstants(
            a = 0.02,
            b = 0.2,
            c = -65.0 + 15 * rE.nextDouble().pow(2),
            d = 8.0 - 6.0 * rE.nextDouble().pow(2)))
    }
    val iNeurons: List<Neuron> = createPopulation(nInh, "inhibitory") { ->
        IzhNeuron(IzhConstants(
            a = 0.02 + 0.08 * rI.nextDouble().pow(2),
            b = 0.25 - 0.05 * rI.nextDouble().pow(2),
            c = -65.0,
            d = 2.0))
    }

    val synapses: ArrayList<Synapse> = arrayListOf()

    for (i in eNeurons.indices) {
        for (j in eNeurons.indices) {
            if (i != j) {
                synapses.add(
                    connectCellsWithSynapse(
                        eNeurons[i],
                        eNeurons[j],
                        false,
                        CurrentSignals(0.0),
                        SynapseSignals(weight = 0.5 * rE.nextDouble())
                    )
                )
            }
        }
        for (j in iNeurons.indices) {
            synapses.add(
                connectCellsWithSynapse(
                    eNeurons[i],
                    iNeurons[j],
                    false,
                    CurrentSignals(0.0),
                    SynapseSignals(weight = 0.5 * rE.nextDouble())
                )
            )
        }
    }

    for (i in iNeurons.indices) {
        for (j in eNeurons.indices) {
            synapses.add(
                connectCellsWithSynapse(
                    iNeurons[i],
                    eNeurons[j],
                    true,
                    CurrentSignals(0.0),
                    SynapseSignals(weight = rI.nextDouble())
                )
            )
        }
        for (j in iNeurons.indices) {
            if (i != j) {
                synapses.add(
                    connectCellsWithSynapse(
                        eNeurons[i],
                        iNeurons[j],
                        true,
                        CurrentSignals(0.0),
                        SynapseSignals(weight = rI.nextDouble())
                    )
                )
            }
        }
    }

    val objects: ArrayList<ModelObject> = arrayListOf()
    objects.addAll(eNeurons)
    objects.addAll(iNeurons)
    objects.addAll(synapses)

    val config = configure {
        timeResolution = microsecond
//        addPathway(synapsePathway())
        addPathway(customIzhPathway())
    }

    val s = Model(objects, config)
    val stopTime = (modelingTime.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}

fun customIzhPathway() = pathway<IzhNeuron> {
    timeResolution = microsecond
    mechanism(mechanism = IzhMechanisms.VDynamic) {
        duration = 500
        condition = Always
    }
    mechanism(mechanism = IzhMechanisms.UDynamic) {
        duration = 500
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.IDynamic) {
        duration = 1000
        condition = Always
        constants = TInputConstants
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
        duration = 500
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
        duration = 500
        condition = { underThresholdAndSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
        duration = 500
        condition = { overThresholdAndNotSpiked(it) }
        constants = SpikeTransferConstants(I_transfer = 1.0)
    }
}