package domain.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response


class HDynamicResponse (override val response: String, override val sourceObject: ModelObject,
                       val signalId: Int) : Response