package org.jetbrains.research.mads.core.telemetry

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.jetbrains.research.mads.core.simulation.Model
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

val format = Json {
    prettyPrint = true
    allowSpecialFloatingPointValues = true
}

class JsonModelExporter(out: Path) {
    private var isClosed = false
    private val outputStream = out.toFile().outputStream()
    private var counter = 0

    init {
        outputStream.write("[".encodeToByteArray())
    }

    fun write(model: Model) {
        if (counter != 0) {
            outputStream.write(",".encodeToByteArray())
        }
        format.encodeToStream(ModelStateSerializer, model, outputStream)
        counter++
    }

    fun close() {
        outputStream.write("]".encodeToByteArray())
        outputStream.flush()
        outputStream.close()
        isClosed = true
    }

}