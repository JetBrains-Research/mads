package org.jetbrains.research.mads.core.types

//typealias MechanismWithParameters = (ModelObject, MechanismParameters) -> Array<Response>
//typealias Mechanism = (ModelObject) -> Array<Response>

//fun parametrizeI(mechanism: MechanismWithParameters, params: MechanismParameters) : Mechanism {
//    return fun(o: ModelObject): Array<Response> {
//        return mechanism(o, params)
//    }
//}

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