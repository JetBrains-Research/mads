package org.jetbrains.research.mads.core.telemetry

import org.jetbrains.research.mads.core.types.Response

object EmptySaver : ResponseSaver {
    override fun logResponse(tick: Long, response: Response): Response {
        return response
    }
}