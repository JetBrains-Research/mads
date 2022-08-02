package org.jetbrains.research.mads.core.types

fun <MO : ModelObject, MP : MechanismParameters> applyParametersToMechanism(mechanism: (MO, MP) -> Array<Response>, params: MP) : ((MO) -> Array<Response>) {
    return fun(obj: MO): Array<Response> {
        return mechanism(obj, params)
    }
}

fun <MO: ModelObject> applyObjectToMechanism(mechanism: (MO) -> Array<Response>, obj: MO) : () -> Array<Response> {
    return fun (): Array<Response> {
        return mechanism(obj)
    }
}