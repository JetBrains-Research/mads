package org.jetbrains.research.mads.core.types

data class AddObjectResponse(override val response: String,
                             override val sourceObject: ModelObject,
                             val addedObject: ModelObject): Response