package org.jetbrains.research.mads.examples

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Structure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import kotlin.io.path.Path
import kotlin.reflect.KProperty1

fun runExperiment(
    logFolder: String,
    logSignals: List<KProperty1<*, *>>,
    logTypes: List<String>,
    topology: Structure,
    config: Configuration,
    stopCondition: (Model) -> Boolean
) {
    val dir = Path("log/${logFolder}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }
    logTypes.forEach { saver.addObjectTypes(it) }

    val allTypes = logTypes.ifEmpty { topology.getAllObjects().distinctBy { it.second.type }.map { it.second.type } }

    allTypes.forEach { type ->
        logSignals.forEach { signal ->
            saver.addObjTypeSignalFilter(type, signal)
        }
    }

    val s = Model(topology, config)

    // logging first state
    //s?.let { saver.logState(it) }

    s?.simulate(saver) { stopCondition.invoke(s) }

    // logging last state
    //s?.let { saver.logState(it) }

    saver.closeModelWriters()
}

fun runExperiment(
    logFolder: String,
    logFilter: Map<String, Set<KProperty1<*, *>>>,
    topology: Structure,
    config: Configuration,
    stopCondition: (Model) -> Boolean
) {
    val dir = Path("log/${logFolder}")
    val saver = FileSaver(dir)
    logFilter.forEach { type ->
        type.value.forEach { signal ->
            saver.addObjTypeSignalFilter(type.key, signal)
        }
    }

    val s = Model(topology, config)

    // logging first state
    //s?.let { saver.logState(it) }

    s?.simulate(saver) { stopCondition.invoke(s) }

    // logging last state
    //s?.let { saver.logState(it) }

    saver.closeModelWriters()
}