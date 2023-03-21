package org.jetbrains.research.experiments.training

import org.jetbrains.research.experiments.connectElectrodeArray
import org.jetbrains.research.experiments.connectPopulations
import org.jetbrains.research.experiments.createPopulation
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.second
import org.jetbrains.research.mads_ns.data_provider.MnistProvider
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.pathways.electrodeArrayPathway
import org.jetbrains.research.mads_ns.pathways.electrodePulsePathway
import org.jetbrains.research.mads_ns.pathways.lifPathway
import org.jetbrains.research.mads_ns.pathways.synapsePathway
import org.jetbrains.research.mads_ns.physiology.neurons.LIFConstants
import org.jetbrains.research.mads_ns.physiology.neurons.LIFNeuron
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.SpikesSignals
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

fun main() {
    val startTime = System.currentTimeMillis()
    val modelingTime = 10 * second  // approx 100 samples
//    val randomSeed = 12345L
    println("Experiment start time $startTime")

    learningExperiment(
        logFolder = "lif/${startTime}",
        { -> LIFNeuron(LIFConstants.V_thresh) },
        configure {
            timeResolution = microsecond
            addPathway(electrodePulsePathway())
            addPathway(synapsePathway())
            addPathway(electrodeArrayPathway())
            addPathway(lifPathway())
        }, modelingTime
    )
}

fun learningExperiment(logFolder: String, neuronFun: () -> Neuron, config: Configuration, time: Double) {
    val dir = Path("log/learningExcMnist/${logFolder}")
    val saver = FileSaver(dir)
    saver.addSignalsNames(SpikesSignals::spiked)    // writing some spikes

    val nExc = 64

    val targetClasses = arrayListOf("1", "3")
    // TODO use relative path
    val dataDir = Paths.get("data/MNIST_training/")
    val provider = MnistProvider(dataDir.absolutePathString(), targetClasses)
    val electrodesArray = ElectrodeArray(provider, 10.0)
    val firstLayer: List<Neuron> = createPopulation(electrodesArray.capacity(), "firstLayer", neuronFun)
    val secondLayer: List<Neuron> = createPopulation(nExc, "secondLayer", neuronFun)
    connectElectrodeArray(electrodesArray, firstLayer)
    val synapses: List<Synapse> = connectPopulations(firstLayer, secondLayer)

    val objects: ArrayList<ModelObject> = arrayListOf()
    objects.add(electrodesArray)
    objects.addAll(electrodesArray.getChildElectrodes())
    objects.addAll(firstLayer)
    objects.addAll(secondLayer)
    objects.addAll(synapses)

    val s = Model(objects, config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}