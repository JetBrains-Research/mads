package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.*
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

data class ConfiguredMechanism<MO : ModelObject>(
    val mechanism: ((MO) -> List<Response>),
    val duration: Int,
    val condition: ((MO) -> Boolean),
)

class ConfiguredMechanismBuilder<MO : ModelObject>(private val mechanism: ((MO, MechanismParameters) -> List<Response>),
                                                                             private val pathwayResolution: Double) {
    var constants: MechanismConstants = EmptyConstants
    var duration: Int = 1
    var condition: ((MO) -> Boolean) = Always

    fun build() = ConfiguredMechanism(applyParametersToMechanism(mechanism, createParams()), duration, condition)

    private fun createParams() : MechanismParameters {
        val timeResolution = (mechanism as CallableReference).findAnnotation<TimeResolution>()
        val constantType = (mechanism as CallableReference).findAnnotation<ConstantType>()
        val dt: Double = if (timeResolution != null)
            pathwayResolution.toBigDecimal().multiply(duration.toBigDecimal()).divide(timeResolution.resolution.toBigDecimal()).toDouble()
        else
            1.0

        if (constantType != null && constants::class != constantType.type)
            constants = constantType.type.createInstance() as MechanismConstants

        return MechanismParameters(constants, dt)
    }
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