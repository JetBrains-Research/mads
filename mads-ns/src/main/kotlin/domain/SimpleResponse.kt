package domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

class SimpleResponse(override val response: String, override val sourceObject: ModelObject) : Response