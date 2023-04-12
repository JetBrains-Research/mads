package org.jetbrains.research.mads.core.telemetry

import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject
import java.io.File
import java.nio.file.Path
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

class FileSaver(dir: Path) : Saver {
    private val modelSignalsWriter : FileWriterThread
    private val modelObjectsWriter : FileWriterThread
    private val modelStateWriter : JsonModelExporter

    private val sigSet = mutableSetOf<String>()

    init {
        mkdirs(dir)
        val signalsFileName = "signals.csv"
        val objectsFileName = "objects.csv"
        val signalsHeader = "time,object,type,signal,value\n"
        val objectsHeader = "time,parent,object,type,action\n"
        val stateFileName = "state.json"
        modelSignalsWriter = FileWriterThread(dir.resolve(signalsFileName), signalsHeader)
        modelObjectsWriter = FileWriterThread(dir.resolve(objectsFileName), objectsHeader)
        modelStateWriter = JsonModelExporter(dir.resolve(stateFileName))

        modelSignalsWriter.start()
        modelObjectsWriter.start()
    }

    override fun addSignalsNames(signal: KProperty<*>) {
        sigSet.add("${signal.javaField?.declaringClass?.simpleName}.${signal.name}")
    }

    override fun logChangedState(tick: Long, obj: ModelObject) {
        val signals = obj.getChangedSignals()
        val objects = obj.getChangedObjects()
        val id = obj.hashCode().toString()
        val type = obj.type
//        scope.launch {
            logSignals(tick, id, type, signals)
            logObjects(tick, id, objects)
//        }
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

        modelSignalsWriter.join()
        modelObjectsWriter.join()
    }

    private fun mkdirs(dir: Path) {
        File(dir.toUri()).mkdirs()
    }

    private fun logSignals(tick: Long, id: String, type: String, signals: Map<String, String>) {
        signals.forEach { signal ->
            if (sigSet.contains(signal.key)) {
                modelSignalsWriter.addStringToQueue(
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

    private fun logObjects(tick: Long, id: String, objects: Map<String, List<String>>) {
        objects.forEach {
            modelObjectsWriter.addStringToQueue(
                (arrayOf(
                    tick.toString(),
                    id,
                    it.key,
                    it.value[0],
                    it.value[1]
                )).joinToString(",") + "\n"
            )
        }
    }
}