package domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

data class SimpleResponse(override val response: String,
                          override val sourceObject: ModelObject,
                          override val log: (Long, Response) -> Response
): Response