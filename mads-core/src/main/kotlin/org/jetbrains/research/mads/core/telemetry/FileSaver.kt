package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import org.jetbrains.research.mads.core.simulation.Model
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class FileSaver(dir: Path) : Saver {
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private val modelChangesWriter : CsvModelExporter
    private val modelStateWriter : JsonModelExporter
    private val sigSet = mutableSetOf<String>()

    init {
        mkdirs(dir)
        val signalsFileName = "signals.csv"
        val signalsHeader = "time,object,type,signal,value\n"
        val stateFileName = "state.json"
        modelChangesWriter = CsvModelExporter(dir.resolve(signalsFileName), signalsHeader)
        modelStateWriter = JsonModelExporter(dir.resolve(stateFileName))

        modelChangesWriter.open()
    }

    override fun addSignalsNames(signal: KProperty<*>) {
        sigSet.add("${signal.javaField?.declaringClass?.simpleName}.${signal.name}")
    }

    override fun logChangedSignals(tick: Long, id: Int, type: String, signals: Map<String, String>) {
        scope.launch {
            signals.forEach { signal ->
                if (sigSet.contains(signal.key)) {
                    modelChangesWriter.write(
                        (arrayOf(
                            tick.toString(),
                            id.toString(),
                            type,
                            signal.key,
                            signal.value
                        )).joinToString(",") + "\n"
                    )
                }
            }
        }
    }

    override fun logState(model: Model) {
//        scope.launch {
            modelStateWriter.write(model)
//        }
    }

    fun closeModelWriters() {
        modelChangesWriter.close()
        modelStateWriter.close()
    }

    private fun mkdirs(dir: Path) {
        File(dir.toUri()).mkdirs()
    }
}