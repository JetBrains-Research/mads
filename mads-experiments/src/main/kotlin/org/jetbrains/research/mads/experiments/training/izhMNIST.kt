package org.jetbrains.research.mads.experiments.training

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads.ns.*
import org.jetbrains.research.mads.ns.electrode.ElectrodeArray
import org.jetbrains.research.mads.ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads.ns.electrode.PulseConstants
import org.jetbrains.research.mads.ns.pathways.electrodeArrayPathway
import org.jetbrains.research.mads.ns.pathways.overThresholdAndNotSpiked
import org.jetbrains.research.mads.ns.pathways.synapsePathway
import org.jetbrains.research.mads.ns.pathways.underThresholdAndSpiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.providers.MnistProvider
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.math.sqrt
import kotlin.reflect.KProperty

fun main() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 1 * second  // approx 200 samples
    println("Experiment start time $startTime")

    learningExperimentIzh(
        logFolder = "izh/${startTime}",
        { -> IzhNeuron(IzhRS, STDPSignals()) },
        { -> IzhNeuron(IzhFS, STDPSignals()) },
        configure {
            timeResolution = microsecond
            addPathway(electrodePulsePathway())
            addPathway(synapsePathway())
            addPathway(electrodeArrayPathway())
            addPathway(izhPathway())
        }, modelingTime
    )
}

fun learningExperimentIzh(logFolder: String, excNeuronFun: () -> Neuron, inhNeuronFun: () -> Neuron, config: Configuration, time: Double) {
    val dir = Path("log/learningExcMnist/${logFolder}")
    val saver = FileSaver(dir)

    val logSignals = arrayListOf<KProperty<*>>(
            SpikesSignals::spiked,
//            SynapseSignals::weight,
//            STDPSignals::stdpTrace,
//            PotentialSignals::V,
//            CurrentSignals::I_e
    )
    logSignals.forEach { saver.addSignalsNames(it) }

    val nExc = 64

    val targetClasses = arrayListOf("1", "0")
    // TODO use relative path
    val dataDir = Paths.get("data/MNIST_training/")
    val provider = MnistProvider(dataDir.absolutePathString(), targetClasses)
    val electrodesArray = ElectrodeArray(provider, 10.0)
    val firstLayer: List<Neuron> = createPopulation(electrodesArray.capacity(), "firstLayer", excNeuronFun)
    val secondLayer: List<Neuron> = createPopulation(nExc, "secondLayer", excNeuronFun)
    val thirdLayer: List<Neuron> = createPopulation(nExc, "thirdLayer", inhNeuronFun)

    connectElectrodeArray(electrodesArray, firstLayer)

    val rnd = Random(42L)

    val synapses: List<Synapse> = connectPopulations(firstLayer, secondLayer) { sqrt(rnd.nextDouble() * 9) }
    val synapses2to3: List<Synapse> = connectPopulationsOneToOne(secondLayer, thirdLayer) { sqrt(rnd.nextDouble() * 9) }
    val synapses3to2: List<Synapse> = connectPopulationsInhibition(thirdLayer, secondLayer) { sqrt(rnd.nextDouble() * 9) }

    val objects: ArrayList<ModelObject> = arrayListOf()
    objects.add(electrodesArray)
    objects.addAll(electrodesArray.getChildElectrodes())
    objects.addAll(firstLayer)
    objects.addAll(secondLayer)
    objects.addAll(thirdLayer)
    objects.addAll(synapses)
    objects.addAll(synapses2to3)
    objects.addAll(synapses3to2)

    val s = Model(objects, config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}

fun electrodePulsePathway() = pathway {
    timeResolution = millisecond
    mechanism(mechanism = ElectrodeMechanisms.PulseDynamic) {
        duration = 10
        condition = Always
        constants = PulseConstants()
    }
}

fun izhPathway() = pathway<IzhNeuron> {
    timeResolution = microsecond
    mechanism(mechanism = IzhMechanisms.VDynamic) {
        duration = 500
        condition = Always
    }
    mechanism(mechanism = IzhMechanisms.UDynamic) {
        duration = 500
        condition = Always
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
        duration = 1
        condition = { underThresholdAndSpiked(it) }
    }
    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
        constants = SpikeTransferConstants(I_transfer = 1.0)
    }
    mechanism(mechanism = NeuronMechanisms.STDPSpike) {
        duration = 1
        condition = { overThresholdAndNotSpiked(it) }
    }

    mechanism(mechanism = NeuronMechanisms.STDPDecay) {
        duration = 1000
    }
}