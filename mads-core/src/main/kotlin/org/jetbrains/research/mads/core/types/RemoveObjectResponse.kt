package org.jetbrains.research.mads.core.types

data class RemoveObjectResponse(override val response: String,
                                override val sourceObject: ModelObject,
                                val removedObject: ModelObject): Response
