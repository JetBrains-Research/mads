package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

class DummyObject: ModelObject() {

    override fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }
}

fun DummyObject.dummyMechanism(params: SimpleParameters) : List<Response> {
    return arrayListOf()
}