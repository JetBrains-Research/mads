package org.jetbrains.research.mads.core.telemetry

import kotlinx.serialization.json.Json
import org.jetbrains.research.mads.core.simulation.Model
import java.io.File
import java.nio.file.Path

val format = Json { prettyPrint = true }

class JsonModelExporter(out: Path) {
    private var isClosed = false
    private val fileWriter = File(out.toUri()).writer()
    private var counter = 0

    init {
        fileWriter.write("[")
    }

    fun write(model: Model) {
        if (counter != 0) {
            fileWriter.write(",")
        }
        fileWriter.write(Json.encodeToString(ModelStateSerializer, model))
        counter++
    }

    fun close() {
        fileWriter.write("]")
        fileWriter.flush()
        fileWriter.close()
        isClosed = true
    }
}