package org.jetbrains.research.mads.core.types

import kotlin.math.abs

data class MechanismParameters(
    val constants: MechanismConstants,
    val dt: Double
)

fun <MO : ModelObject> applyParametersToMechanism(
    mechanism: (MO, MechanismParameters) -> List<Response>,
    params: MechanismParameters
): ((MO) -> List<Response>) {
    return fun(obj: MO): List<Response> {
        return mechanism(obj, params)
    }
}

fun <MO : ModelObject> applyObjectToMechanism(
    mechanism: (MO) -> List<Response>,
    obj: MO
): () -> List<Response> {
    return fun(): List<Response> {
        return mechanism(obj)
    }
}

fun signalDecay(signal: Double, decayConstants: DecayConstants, dt: Double) : Double {
    return if (abs(signal) <= decayConstants.zeroingLimit) {
        -signal
    } else {
        -decayConstants.decayMultiplier * signal * dt
    }
}