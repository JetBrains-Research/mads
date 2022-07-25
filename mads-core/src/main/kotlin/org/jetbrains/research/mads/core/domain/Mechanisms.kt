package org.jetbrains.research.mads.core.domain

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

fun simpleMechanism(o: ModelObject, params: MechanismParameters) : Array<Response> {
    println(o.type)
    println(params.duration)
    println((params as SimpleParameters).probability)
    return arrayOf(SimpleResponse())
}