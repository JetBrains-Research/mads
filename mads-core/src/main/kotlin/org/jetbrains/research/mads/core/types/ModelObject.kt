package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.desd.ModelEvent

abstract class ModelObject {
    open val type = "Model Object"

    val events : ArrayList<ModelEvent<ModelObject>> = ArrayList()
}

fun <MO: ModelObject, MP: MechanismParameters> wrapMechanism(obj: MO,
                                                             mechanism: (MO, MP) -> Array<Response>,
                                                             params: MP) {
    obj.events.add(ModelEvent(parametrize(mechanism, params), obj, params.duration))
}