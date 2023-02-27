package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import org.jetbrains.research.mads.core.types.Response
import java.io.File
import java.nio.file.Paths

object FileSaver : ResponseSaver {
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private lateinit var modelWriter : CsvModelExporter

    fun initModelWriters(dir: String) {
        mkdirs(dir)
        val csvModelExporter = CsvModelExporter()
        csvModelExporter.open(
            Paths.get(dir, File.separator),
            "responses.csv",
            ""
        )
        modelWriter = csvModelExporter
    }

    override fun logResponse(tick: Long, response: Response): Response {
        scope.launch {
            modelWriter.flow.emit(
                (arrayOf(
                tick.toString(),
                response.sourceObject.hashCode().toString(),
                response.sourceObject.type.toString(),
                response.logLabel,
                response.logValue)).joinToString(",")+"\n")
        }

        return response
    }

    fun closeModelWriters() {
        modelWriter.close()
    }

    private fun mkdirs(dir: String) {
        File(dir).mkdirs()
    }
}