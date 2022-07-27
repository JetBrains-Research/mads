package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.desd.ModelEvent
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import kotlin.reflect.KClass

class Pathway<MO : ModelObject>(val objectType : KClass<MO>) {
    val mechanismConditions : HashMap<((MO) -> Array<Response>), ((MO) -> Boolean)> = HashMap()

    fun add(mechanism: (MO) -> Array<Response>, condition: (MO) -> Boolean) {
        mechanismConditions[mechanism] = condition
    }

//    fun createEvents() : List<ModelEvent<MO>> {
//        mechanismConditions.forEach {  }
//    }
}