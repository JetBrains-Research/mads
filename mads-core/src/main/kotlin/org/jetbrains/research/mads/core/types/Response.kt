package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.telemetry.EmptySaver

class Response(
    val sourceObject: ModelObject,
    val logLabel: String,
    val logValue: String,
    val applyFn: () -> Unit
) {
    var logFn: (Long, Response) -> Response = EmptySaver::logResponse
}