package org.jetbrains.research.mads.core.simulation

import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ProgressBarRotating(
    private val delay: Long,
    private var additionalInfo: String = ""
) {
    private val status: String = "status:"
    private val modelingTimeFormatter = SimpleDateFormat("HH:mm:ss:SSS")
    private val scope = CoroutineScope(Dispatchers.IO + Job() + SupervisorJob())
    private var showProgress = false
    private var duration = getModelingTime(0L)
    private val frames = arrayOf(
        "|",
        "/",
        "-",
        "\\"
    )

    init {
        modelingTimeFormatter.timeZone = TimeZone.getTimeZone("GMT")
        print("\r[$duration] $additionalInfo | $status initialization")
    }

    fun start() {
        var x = 0
        showProgress = true
        scope.launch {
            while (showProgress) {
                print("\r[$duration] $additionalInfo | $status ${frames[x++ % frames.size]}")
                delay(delay)
            }
        }
    }

    fun updateInfo(time: Long, info: String) {
        duration = getModelingTime(time)
        additionalInfo = info
    }

    fun stop(extMessage: String) {
        showProgress = false
        print("\r[$duration] $additionalInfo | $status $extMessage")
    }

    private fun getModelingTime(time: Long): String {
        val date = Date(time)
        return modelingTimeFormatter.format(date)
    }
}