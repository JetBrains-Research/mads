package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File
import java.io.OutputStreamWriter
import java.nio.file.Path

class CsvModelExporter {
    var isClosed = false
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())

    //lateinit
    lateinit var flow: MutableSharedFlow<List<String>>
    private lateinit var fileWriter: OutputStreamWriter
    private lateinit var jobCollect: Deferred<Unit>

    //region Public
    fun open(path: Path, fileName: String, header: String) {
        openFile(path, fileName, header)
    }

    fun close() {
        scope.launch {
            jobCollect.await()
        }
        fileWriter.flush()
        fileWriter.close()
        isClosed = true
    }

    fun writeSync(data: List<String>) {
        data.forEach {
            write(it)
        }
    }

    //region Private
    private fun openFile(path: Path, fileName: String, header: String) {
        flow = MutableSharedFlow()
        fileWriter = File(path.toString() + File.separator + fileName).writer()
        fileWriter.write(header)

        jobCollect = scope.async {
            flow.collect {
                writeSync(it)
            }
        }
    }

    private fun write(text: String) {
        fileWriter.write(text)
    }
}