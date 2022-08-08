package org.jetbrains.research.mads.core.types.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

class DynamicResponse (override val response: String, override val sourceObject: ModelObject,
                       val delta: Double,
                       val updateFn: (Double) -> Unit) : Response