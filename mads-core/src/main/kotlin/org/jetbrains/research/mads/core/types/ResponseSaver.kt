package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.telemetry.EmptySaver

interface ResponseSaver {
    fun logResponse(tick: Long, response: Response): Response
}

open class SavingParameters(val saver: ResponseSaver, val saveResponse: Boolean)

object EmptySavingParameters: SavingParameters(EmptySaver, false)