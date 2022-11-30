package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*

open class SimpleObject : ModelObject() {
    val rnd = Random(12345L)

    override fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }
}