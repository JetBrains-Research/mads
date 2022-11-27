package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.ResponseSaver
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass

object FileSaver : ResponseSaver {
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private val modelWriters = HashMap<KClass<out Response>, CsvModelExporter>()

    fun initModelWriters(dir: String, allowedResponses: Set<KClass<out Response>>) {
        mkdirs(dir)
        allowedResponses.forEach {
            val csvModelExporter = CsvModelExporter()
            csvModelExporter.open(
                Paths.get(dir, File.separator),
                "${it.simpleName}s.csv",
                ""
            )
            modelWriters[it] = csvModelExporter
        }
    }

    override fun logResponse(tick: Long, response: Response): Response {
        if (modelWriters.containsKey(response::class)) { //} && response.logResponse) {
            scope.launch {
                modelWriters[response::class]!!.flow.emit("${tick}, " + response.response)
            }
        }
        return response
    }

    fun closeModelWriters() {
        modelWriters.values.forEach {
            it.close()
        }
    }

    fun isClosed(): Boolean {
        return (modelWriters.values.map { it.isClosed }.toList())
            .reduceOrNull { acc, next -> acc && next }
            ?: true
    }

    private fun mkdirs(dir: String) {
        File(dir).mkdirs()
    }
}