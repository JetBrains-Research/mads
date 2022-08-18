package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads.core.telemetry.FileSaver

interface ResponseSaver {
    fun logResponse(tick: Long, response: Response): Response
}

open class SavingParameters(val saver: ResponseSaver, val saveResponse: Boolean)

object SkipSaving: SavingParameters(EmptySaver, false)

object SaveToFile: SavingParameters(FileSaver, true)