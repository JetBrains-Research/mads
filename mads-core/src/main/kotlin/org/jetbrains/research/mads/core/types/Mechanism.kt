package org.jetbrains.research.mads.core.types

data class MechanismParameters(
    val constants: Constants,
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