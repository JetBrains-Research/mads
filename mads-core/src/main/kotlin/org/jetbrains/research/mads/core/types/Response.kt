package org.jetbrains.research.mads.core.types

data class Response(
    val sourceObject: ModelObject,
    val conflict: Conflict,
    val applyFn: () -> Unit,
)

val EmptyResponseList = listOf<Response>()