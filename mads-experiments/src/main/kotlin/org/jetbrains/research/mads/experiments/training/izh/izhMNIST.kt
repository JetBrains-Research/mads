package org.jetbrains.research.mads.experiments.training.izh

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.experiments.training.mnistTopology
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.providers.MnistProvider
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.reflect.KProperty

fun main() {
    mnist3Phase()
}

fun mnist3Phase() {
    val startTime = System.currentTimeMillis()
    val trainClassSize = 20 // per class
    val assignClassSize = 20 // per class
    val testClassSize = 20 // per class
    val nExc = 64

    val targetClasses = listOf("1", "0")
    // TODO use relative path
    val dataDir = Paths.get("data/MNIST_training/")
    val provider = MnistProvider(dataDir.absolutePathString(), targetClasses)
    val topology = mnistTopology(
        provider,
        { -> IzhNeuron(IzhRS, STDPSignals()) },
        { -> IzhNeuron(IzhFS, STDPSignals()) },
        nExc
    )

    val trainSize = trainClassSize * targetClasses.size
    val assignSize = assignClassSize * targetClasses.size
    val testSize = testClassSize * targetClasses.size
    println("Experiment start time $startTime")

    learningPhase(
        logFolder = "train/${startTime}",
        listOf(),
        listOf(),
        topology,
        trainPhaseConfig()
    ) { provider.imageIndex >= trainSize }
    learningPhase(
        logFolder = "assign/${startTime}",
        listOf(SpikesSignals::spikeCounter, CurrentStimuli::stimuli),
        listOf("inputLayer", "secondLayer"),
        topology,
        testPhaseConfig()
    ) { provider.imageIndex >= trainSize + assignSize }
    learningPhase(
        logFolder = "test/${startTime}",
        listOf(SpikesSignals::spikeCounter, CurrentStimuli::stimuli),
        listOf("inputLayer", "secondLayer"),
        topology,
        testPhaseConfig()
    ) { provider.imageIndex >= trainSize + assignSize + testSize }
}

fun learningPhase(
    logFolder: String,
    logSignals: List<KProperty<*>>,
    logTypes: List<String>,
    topology: List<ModelObject>,
    config: Configuration,
    stopCondition: () -> Boolean
) {
    val dir = Path("log/mnist/${logFolder}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }
    logTypes.forEach { saver.addObjectTypes(it) }

    val s = Model(topology, config)
    s?.simulate(saver) { stopCondition.invoke() }
    saver.closeModelWriters()
}