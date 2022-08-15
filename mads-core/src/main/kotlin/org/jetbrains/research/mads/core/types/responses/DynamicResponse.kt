package org.jetbrains.research.mads.core.types.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

data class DynamicResponse(
    override val response: String,
    override val sourceObject: ModelObject,
    override val logFunction: (Long, Response) -> Response,
    override val logResponse: Boolean,
    val delta: Double,
    val updateFn: (Double) -> Unit
) : Response