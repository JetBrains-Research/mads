package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.*
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.full.findAnnotation

data class ConfiguredMechanism<MO : ModelObject>(
    val mechanism: ((MO) -> List<Response>),
    val duration: Int,
    val condition: ((MO) -> Boolean),
)

class ConfiguredMechanismBuilder<MO : ModelObject>(private val mechanism: ((MO, MechanismParameters) -> List<Response>),
                                                                             private val pathwayResolution: Double) {
    var constants: Constants = EmptyConstants
    var duration: Int = 1
    var condition: ((MO) -> Boolean) = Always

    fun build() = ConfiguredMechanism(applyParametersToMechanism(mechanism, createParams()), duration, condition)

    private fun createParams() : MechanismParameters {
        val annotation = (mechanism as CallableReference).findAnnotation<TimeResolutionAnnotation>()
        val dt: Double = if (annotation != null)
            pathwayResolution.toBigDecimal().multiply(duration.toBigDecimal()).divide(annotation.resolution.toBigDecimal()).toDouble()
        else
            1.0

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