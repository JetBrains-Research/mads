package org.jetbrains.research.mads.core.types

interface Response {
    val response: String
    val sourceObject: ModelObject
    val log: (Long, Response) -> Response
}