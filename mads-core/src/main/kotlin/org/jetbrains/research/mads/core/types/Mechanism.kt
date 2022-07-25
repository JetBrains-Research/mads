package org.jetbrains.research.mads.core.types

//typealias MechanismWithParameters = (ModelObject, MechanismParameters) -> Array<Response>
//typealias Mechanism = (ModelObject) -> Array<Response>

//fun parametrizeI(mechanism: MechanismWithParameters, params: MechanismParameters) : Mechanism {
//    return fun(o: ModelObject): Array<Response> {
//        return mechanism(o, params)
//    }
//}

fun <MO: ModelObject, MP: MechanismParameters> parametrize(mechanism: (MO, MP) -> Array<Response>, params: MP) : ((MO) -> Array<Response>) {
    return fun(o: MO): Array<Response> {
        return mechanism(o, params)
    }
}