package org.jetbrains.research.mads.core.types.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

interface SignalChangeResponse: Response

abstract class SignalBooleanChangeResponse(
    override val response: String,
    override val sourceObject: ModelObject,
//    override val logFunction: (Long, Response) -> Response,
//    override val logResponse: Boolean,
    open val value: Boolean,
    open val updateFn: (Boolean) -> Unit
) : SignalChangeResponse

abstract class SignalIntChangeResponse(
    override val response: String,
    override val sourceObject: ModelObject,
//    override val logFunction: (Long, Response) -> Response,
//    override val logResponse: Boolean,
    open val value: Int,
    open val updateFn: (Int) -> Unit
) : SignalChangeResponse

abstract class SignalDoubleChangeResponse(
    override val response: String,
    override val sourceObject: ModelObject,
//    override val logFunction: (Long, Response) -> Response,
//    override val logResponse: Boolean,
    open val value: Double,
    open val updateFn: (Double) -> Unit
) : SignalChangeResponse