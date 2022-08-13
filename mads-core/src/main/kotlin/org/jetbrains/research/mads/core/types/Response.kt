package org.jetbrains.research.mads.core.types

interface Response {
    val response: String
    val sourceObject: ModelObject
    val logFunction: (Long, Response) -> Response
    val logResponse: Boolean
}