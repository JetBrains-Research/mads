package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class FileSaver(dir: String) : Saver {
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private var modelWriter : CsvModelExporter = CsvModelExporter()
    private val sigSet = mutableSetOf<String>()

    init {
        mkdirs(dir)
        val csvModelExporter = CsvModelExporter()
        csvModelExporter.open(
            Paths.get(dir, File.separator),
            "signals.csv",
            "time,object,type,signal,value\n"
        )
        modelWriter = csvModelExporter
    }

    override fun addSignalsNames(signal: KProperty<*>) {
        sigSet.add("${signal.javaField?.declaringClass?.simpleName}.${signal.name}")
    }

    override fun logChangedSignals(tick: Long, id: Int, type: String, signals: Map<String, String>) {
        scope.launch {
            signals.forEach { signal ->
                if (sigSet.contains(signal.key)) {
                    modelWriter.write(
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

    fun closeModelWriters() {
        modelWriter.close()
    }

    private fun mkdirs(dir: String) {
        File(dir).mkdirs()
    }
}