package org.jetbrains.research.mads.core.types

data class Response(
    val sourceObject: ModelObject,
    val resolveFn: (List<Response>) -> List<Response>,
    val applyFn: () -> Unit,
)

val EmptyResponseList = listOf<Response>()