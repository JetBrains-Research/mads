package org.jetbrains.research.mads.experiments.training.izh

import org.jetbrains.research.mads.experiments.training.SynapsesParameters
import org.jetbrains.research.mads.experiments.training.Topology
import org.jetbrains.research.mads.experiments.training.runExperiment
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.providers.MnistProvider
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

fun main() {
    mnist3PhaseIzh()
}

fun mnist3PhaseIzh() {
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
        { -> IzhNeuron(IzhRS, adaptiveThreshold = true, weightNormalizationEnabled = true) },
        { -> IzhNeuron(IzhFS, adaptiveThreshold = false, weightNormalizationEnabled = false) },
        nExc,
        rnd,
        listOf(
            SynapsesParameters(weight = { rnd.nextDouble() / 2 }, delay = { rnd.nextInt(100) * 100 }),
            SynapsesParameters(weight = { 5.0 }, delay = { 0 }),
            SynapsesParameters(weight = { 5.0 }, delay = { 0 })
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
        topology,
        trainPhaseIzhConfig()
    ) { provider.imageIndex >= trainSize }
    runExperiment(
        logFolder = "assign/${startTime}",
        mapOf(
            Topology.INPUT_LAYER to hashSetOf(CurrentStimuli::stimuli),
            Topology.SECOND_LAYER to hashSetOf(SpikesSignals::spikeCounter)
        ),
        topology,
        testPhaseIzhConfig()
    ) { provider.imageIndex >= trainSize + assignSize }
    runExperiment(
        logFolder = "test/${startTime}",
        listOf(SpikesSignals::spikeCounter, CurrentStimuli::stimuli),
        listOf(Topology.INPUT_LAYER, Topology.SECOND_LAYER),
        topology,
        testPhaseIzhConfig()
    ) { provider.imageIndex >= trainSize + assignSize + testSize }
}