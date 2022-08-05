package org.jetbrains.research.mads.core.types.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

data class RemoveObjectResponse(override val response: String,
                                override val sourceObject: ModelObject,
                                val removedObject: ModelObject
): Response
