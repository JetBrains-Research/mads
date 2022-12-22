package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.telemetry.EmptySaver

class Response(
    val sourceObject: ModelObject,
    var logLabel: String,
    var logValue: String,
    val applyFn: () -> Unit
) {
    var logFn: (Long, Response) -> Response = EmptySaver::logResponse
}