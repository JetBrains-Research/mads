package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class FileSaver(dir: Path) : Saver {
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private val modelSignalsWriter : CsvModelExporter
    private val modelObjectsWriter : CsvModelExporter
    private val modelStateWriter : JsonModelExporter
    private val sigSet = mutableSetOf<String>()

    init {
        mkdirs(dir)
        val signalsFileName = "signals.csv"
        val objectsFileName = "objects.csv"
        val signalsHeader = "time,object,type,signal,value\n"
        val objectsHeader = "time,parent,object,type,action\n"
        val stateFileName = "state.json"
        modelSignalsWriter = CsvModelExporter(dir.resolve(signalsFileName), signalsHeader)
        modelObjectsWriter = CsvModelExporter(dir.resolve(objectsFileName), objectsHeader)
        modelStateWriter = JsonModelExporter(dir.resolve(stateFileName))

        modelSignalsWriter.open()
        modelObjectsWriter.open()
    }

    override fun addSignalsNames(signal: KProperty<*>) {
        sigSet.add("${signal.javaField?.declaringClass?.simpleName}.${signal.name}")
    }

    override fun logChangedState(tick: Long, obj: ModelObject) {
        val signals = obj.getChangedSignals()
        val id = obj.hashCode().toString()
        val type = obj.type
        scope.launch {
            logSignals(tick, id, type, signals)
//            logObjects(tick, obj)

        }
    }

    override fun logState(model: Model) {
//        scope.launch {
            modelStateWriter.write(model)
//        }
    }

    fun closeModelWriters() {
        modelSignalsWriter.close()
        modelObjectsWriter.close()
        modelStateWriter.close()
    }

    private fun mkdirs(dir: Path) {
        File(dir.toUri()).mkdirs()
    }

    private suspend fun logSignals(tick: Long, id: String, type: String, signals: Map<String, String>) {
        signals.forEach { signal ->
            if (sigSet.contains(signal.key)) {
                modelSignalsWriter.write(
                    (arrayOf(
                        tick.toString(),
                        id,
                        type,
                        signal.key,
                        signal.value
                    )).joinToString(",") + "\n"
                )
            }
        }
    }

    private suspend fun logObjects(tick: Long, obj: ModelObject) {

        // TODO: rewrite without leaking actual ModelObjects. Have to
        // make getChangedObjects() fun to return a bunch of strings
        obj.getChangedObjects().forEach {
            modelObjectsWriter.write(
                (arrayOf(
                    tick.toString(),
                    it.key.parent.hashCode().toString(),
                    it.key.hashCode().toString(),
                    it.key.type,
                    it.value
                )).joinToString(",") + "\n"
            )
        }
    }
}