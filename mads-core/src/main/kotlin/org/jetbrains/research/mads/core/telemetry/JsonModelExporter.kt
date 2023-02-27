package org.jetbrains.research.mads.core.telemetry

import java.nio.file.Path
import kotlin.io.path.Path
import java.io.OutputStreamWriter
import java.io.File

import kotlinx.serialization.json.*
import org.jetbrains.research.mads.core.simulation.Model

val format = Json { prettyPrint = true }

class JsonModelExporter(out: Path) {
    var isClosed = false
    private val fileWriter = File(out.toUri()).writer()
    var counter = 0

    fun write(model: Model) {
        if (counter == 0) {
            fileWriter.write("[")
        } else {
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