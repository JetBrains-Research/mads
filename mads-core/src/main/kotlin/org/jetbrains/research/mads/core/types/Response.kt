package org.jetbrains.research.mads.core.types

data class Response(
    val sourceObject: ModelObject,
    val applyFn: () -> Unit
)

val EmptyResponseList = listOf<Response>()