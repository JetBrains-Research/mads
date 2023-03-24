package org.jetbrains.research.experiments.population

import org.jetbrains.research.experiments.connectElectrodes
import org.jetbrains.research.experiments.connectPopulations
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
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.NoiseSignals
import org.jetbrains.research.mads_ns.pathways.overThresholdAndNotSpiked
import org.jetbrains.research.mads_ns.pathways.underThresholdAndSpiked
import org.jetbrains.research.mads_ns.physiology.neurons.*
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import java.util.*
import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.reflect.KProperty

fun main() {
    val experimentName = "population"
    val startTime = System.currentTimeMillis()
    val modelingTime = 1000 * millisecond
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

    val nExc = 80 //00
    val nInh = 20 //00

    val rE = Random(randomSeed)
    val rI = Random(randomSeed - 1)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val eNeurons: List<Neuron> = createPopulation(nExc, "excitatory") { ->
        IzhNeuron(IzhConstants(
            a = 0.02,
            b = 0.2,
            c = -65.0 + 15 * rE.nextDouble().pow(2),
            d = 8.0 - 6.0 * rE.nextDouble().pow(2)))
    }
    val iNeurons: List<Neuron> = createPopulation(nInh, "inhibitory") { ->
        IzhNeuron(IzhConstants(
            a = 0.02 + 0.08 * rI.nextDouble(),
            b = 0.25 - 0.05 * rI.nextDouble(),
            c = -65.0,
            d = 2.0))
    }

    eNeurons.forEach {
        val u = it.signals[PotentialSignals::class] as PotentialSignals
        val izh = it.signals[IzhSignals::class] as IzhSignals
        val consts = (it as IzhNeuron).izhType

        izh.U = consts.b*u.V
    }

    iNeurons.forEach {
        val u = it.signals[PotentialSignals::class] as PotentialSignals
        val izh = it.signals[IzhSignals::class] as IzhSignals
        val consts = (it as IzhNeuron).izhType

        izh.U = consts.b*u.V
    }

    objects.addAll(eNeurons)
    objects.addAll(iNeurons)

    val eElectrodes = connectElectrodes(eNeurons) { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = 5.0)) }
    val iElectrodes = connectElectrodes(iNeurons) { seed: Long -> Electrode(Random(seed), CurrentSignals(I_e = 0.0), NoiseSignals(std = 2.0)) }

    objects.addAll(eElectrodes)
    objects.addAll(iElectrodes)

//    val weightMultiplier = 1000/(nExc + nInh)
    val weightMultiplier = 2.5

    val synapses: ArrayList<Synapse> = arrayListOf()
    synapses.addAll(connectPopulations(eNeurons, eNeurons, weight = { weightMultiplier*0.5 * rE.nextDouble() }))
    synapses.addAll(connectPopulations(eNeurons, iNeurons, weight = { weightMultiplier*0.5 * rE.nextDouble() }))
    synapses.addAll(connectPopulations(iNeurons, eNeurons, weight = { -weightMultiplier*rI.nextDouble() }))
    synapses.addAll(connectPopulations(iNeurons, iNeurons, weight = { -weightMultiplier*rI.nextDouble() }))
    objects.addAll(synapses)

    val config = configure {
        timeResolution = microsecond
        addPathway(electrodeNoisePathway())
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
    mechanism(mechanism = NeuronMechanisms.SpikeDecay) {
        duration = 500
        constants = SpikeTransferConstants(I_transfer = 1.0)
    }
}