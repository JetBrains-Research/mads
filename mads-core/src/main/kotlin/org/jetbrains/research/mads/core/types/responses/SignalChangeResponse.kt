package org.jetbrains.research.mads.core.types.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

interface SignalChangeResponse: Response

data class SignalBooleanChangeResponse(
    override val response: String,
    override val sourceObject: ModelObject,
    override val logFunction: (Long, Response) -> Response,
    override val logResponse: Boolean,
    val value: Boolean,
    val updateFn: (Boolean) -> Unit
) : SignalChangeResponse

data class SignalIntChangeResponse(
    override val response: String,
    override val sourceObject: ModelObject,
    override val logFunction: (Long, Response) -> Response,
    override val logResponse: Boolean,
    val value: Int,
    val updateFn: (Int) -> Unit
) : SignalChangeResponse

data class SignalDoubleChangeResponse(
    override val response: String,
    override val sourceObject: ModelObject,
    override val logFunction: (Long, Response) -> Response,
    override val logResponse: Boolean,
    val value: Double,
    val updateFn: (Double) -> Unit
) : SignalChangeResponse