package org.jetbrains.research.mads.core.telemetry

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class FileWriterThread(private val path: Path, private val header: String, private val bufferSize: Int = 64 * 1024) : Thread() {

    private var running = true
    private val queue: BlockingQueue<String> = LinkedBlockingQueue()
    private val buffer: StringBuilder = StringBuilder()

    fun addStringToQueue(string: String) {
        if (running)
            queue.put(string)
    }

    fun close() {
        running = false
    }

    override fun run() {
        val channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        channel.write(ByteBuffer.wrap(header.toByteArray(StandardCharsets.UTF_8)))
        var hasData = false
        while (running || hasData) {
            val string = queue.poll()
            if (string != null) {
                hasData = true
                buffer.append(string)
                if (buffer.length > bufferSize) {
                    channel.write(ByteBuffer.wrap(buffer.toString().toByteArray(StandardCharsets.UTF_8)))
                    buffer.clear()
                }
            } else {
                hasData = false
                sleep(1) // sleep for a short time to avoid busy loop
            }
        }

        // Write any remaining buffer content
        if (buffer.isNotEmpty()) {
            channel.write(ByteBuffer.wrap(buffer.toString().toByteArray(StandardCharsets.UTF_8)))
        }

        channel.close()
    }
}