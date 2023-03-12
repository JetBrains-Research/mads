package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.OutputStreamWriter
import java.nio.file.Path

class CsvModelExporter(path: Path, header: String) {
    private var writer: OutputStreamWriter = initWriter(path, header)
    private lateinit var channel: Channel<String>

    @OptIn(DelicateCoroutinesApi::class)
    fun open() {
        channel = Channel()

        val writerJob = GlobalScope.launch {
            for (message in channel) {
                withContext(Dispatchers.IO) {
                    writer.write(message)
                    writer.flush()
                }
            }
            withContext(Dispatchers.IO) {
                writer.close()
            }
        }

        writerJob.invokeOnCompletion {
            channel.close(it)
        }
    }

    fun close() {
        runBlocking {
            channel.send("")
        }
    }

    suspend fun write(message: String) {
        channel.send(message)
    }

    private fun initWriter(path: Path, header: String) : OutputStreamWriter {
        writer = File(path.toUri()).writer()
        writer.write(header)

        return writer
    }
}