package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.applyParametersToMechanism

data class ConfiguredMechanism<MO : ModelObject>(
    val mechanism: ((MO) -> List<Response>),
    val duration: Int,
    val condition: ((MO) -> Boolean)
)

class MocRecordBuilder<MO : ModelObject, MP : MechanismParameters>(private val mechanism: ((MO, MP) -> List<Response>),
                                                                   private val parameters: MP) {
    var duration: Int = 1
    var condition: ((MO) -> Boolean) = Always

    fun build() = ConfiguredMechanism(applyParametersToMechanism(mechanism, parameters), duration, condition)
}

typealias Predicate<T> = ((T) -> Boolean)

object Always : Predicate<ModelObject> {
    override fun invoke(p1: ModelObject): Boolean {
        return true
    }
}

object Never : Predicate<ModelObject> {
    override fun invoke(p1: ModelObject): Boolean {
        return false
    }
}