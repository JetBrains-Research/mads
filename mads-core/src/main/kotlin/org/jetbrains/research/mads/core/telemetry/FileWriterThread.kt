package org.jetbrains.research.mads.core.telemetry
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class FileWriterThread(private val path: Path, private val header: String) : Thread() {

    private var running = true
    private val queue: BlockingQueue<String> = LinkedBlockingQueue()

    fun addStringToQueue(string: String) {
        if (running)
            queue.put(string)
    }

    fun close() {
        running = false
    }

    override fun run() {
        val writer = BufferedWriter(FileWriter(File(path.toUri())))
        writer.write(header)
        var hasData = false
        while (running || hasData) {
            val string = queue.poll()
            if (string != null) {
                hasData = true
                writer.write(string)
                writer.flush()
            } else {
                hasData = false
                sleep(1) // sleep for a short time to avoid busy loop
            }
        }
        writer.close()
    }
}