package org.jetbrains.research.mads.core.types

interface MechanismParameters {
    val savingParameters: SavingParameters
    val constants: Constants
}

object EmptyParameters : MechanismParameters {
    override val savingParameters: SavingParameters
        get() = EmptySavingParameters
    override val constants: Constants
        get() = EmptyConstants
}

fun <MO : ModelObject, MP : MechanismParameters> applyParametersToMechanism(
    mechanism: (MO, MP) -> List<Response>,
    params: MP
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