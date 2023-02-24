package org.jetbrains.research.mads.core.telemetry

import org.jetbrains.research.mads.core.types.Response

interface ResponseSaver {
    fun logResponse(tick: Long, response: Response): Response
}