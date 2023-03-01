package org.jetbrains.research.mads.core.telemetry

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.OutputStreamWriter
import java.nio.file.Path

class CsvModelExporter {
    private lateinit var writer: OutputStreamWriter
    private lateinit var channel: Channel<String>

    @OptIn(DelicateCoroutinesApi::class)
    fun open(path: Path, fileName: String, header: String) {
        writer = File(path.toString() + File.separator + fileName).writer()
        writer.write(header)
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
}