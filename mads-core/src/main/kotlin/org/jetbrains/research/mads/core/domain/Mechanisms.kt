package org.jetbrains.research.mads.core.domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

fun ModelObject.simpleMechanism(params: SimpleParameters) : Array<Response> {
    return arrayOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability))
}