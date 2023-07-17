package org.jetbrains.research.mads.examples.training.lif

import org.jetbrains.research.mads.core.configuration.Structure
import org.jetbrains.research.mads.examples.SynapsesParameters
import org.jetbrains.research.mads.examples.Topology
import org.jetbrains.research.mads.examples.runExperiment
import org.jetbrains.research.mads.ns.physiology.neurons.AdaptiveLIFNeuron
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentStimuli
import org.jetbrains.research.mads.ns.physiology.neurons.SpikesSignals
import org.jetbrains.research.mads.providers.MnistProvider
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

fun main() {
    mnist3PhaseLif()
}


fun mnist3PhaseLif() {
    val startTime = System.currentTimeMillis()
    val trainClassSize = 20 // per class
    val assignClassSize = 20 // per class
    val testClassSize = 20 // per class
    val nExc = 64

    val targetClasses = listOf("1", "0")
    // TODO use relative path
    val dataDir = Paths.get("data/MNIST_training/")
    val provider = MnistProvider(dataDir.absolutePathString(), targetClasses)
    val rnd = Random(42L)
    val topology = Topology.mnistTopology(
        provider,
        { -> AdaptiveLIFNeuron(adaptiveThreshold = true, weightNormalizationEnabled = true, hasRefracTimer = true) },
        { -> AdaptiveLIFNeuron(adaptiveThreshold = false, weightNormalizationEnabled = false, hasRefracTimer = true) },
        nExc,
        rnd,
        listOf(
            SynapsesParameters(weight = { rnd.nextDouble() / 3 }, delay = { rnd.nextInt(100) * 100 }),
            SynapsesParameters(weight = { 10.5 }, delay = { 0 }),
            SynapsesParameters(weight = { 17.5 }, delay = { 0 })
        )
    )

    val trainSize = trainClassSize * targetClasses.size
    val assignSize = assignClassSize * targetClasses.size
    val testSize = testClassSize * targetClasses.size
    println("Experiment start time $startTime")

    runExperiment(
        logFolder = "train/${startTime}",
        mapOf(
            Topology.INPUT_LAYER to hashSetOf(CurrentStimuli::stimuli),
            Topology.SECOND_LAYER to hashSetOf(SpikesSignals::spikeCounter),
            Topology.OUTPUT_LAYER to hashSetOf(SpikesSignals::spikeCounter)
        ),
        Structure(topology),
        trainPhaseLifConfig()
    ) { provider.imageIndex >= trainSize }
    runExperiment(
        logFolder = "assign/${startTime}",
        mapOf(
            Topology.INPUT_LAYER to hashSetOf(CurrentStimuli::stimuli),
            Topology.SECOND_LAYER to hashSetOf(SpikesSignals::spikeCounter)
        ),
        Structure(topology),
        testPhaseLifConfig()
    ) { provider.imageIndex >= trainSize + assignSize }
    runExperiment(
        logFolder = "test/${startTime}",
        mapOf(
            Topology.INPUT_LAYER to hashSetOf(CurrentStimuli::stimuli),
            Topology.SECOND_LAYER to hashSetOf(SpikesSignals::spikeCounter)
        ),
        Structure(topology),
        testPhaseLifConfig()
    ) { provider.imageIndex >= trainSize + assignSize + testSize }
}
