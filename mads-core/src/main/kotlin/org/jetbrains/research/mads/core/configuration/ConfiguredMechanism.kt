package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.*
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.full.findAnnotation
import kotlin.system.exitProcess

data class ConfiguredMechanism<MO : ModelObject>(
    val mechanism: ((MO) -> List<Response>),
    val duration: Int,
    val delay: (MO) -> Int,
    val condition: ((MO) -> Boolean),
)

class ConfiguredMechanismBuilder<MO : ModelObject>(private val mechanism: ((MO, MechanismParameters) -> List<Response>),
                                                                             private val pathwayResolution: Double) {
    var constants: MechanismConstants = EmptyConstants
    var duration: Int = 1
    var delay: (MO) -> Int = { 0 }
    var condition: ((MO) -> Boolean) = Always

    private val callRef = mechanism as CallableReference

    fun build() =
        ConfiguredMechanism(applyParametersToMechanism(mechanism, createParams()), checkDuration(), delay, condition)

    private fun createParams(): MechanismParameters {
        val timeResolution = callRef.findAnnotation<TimeResolution>()
        val constantType = callRef.findAnnotation<ConstantType>()
        val dt: Double = if (timeResolution != null)
            pathwayResolution.toBigDecimal().multiply(duration.toBigDecimal())
                .divide(timeResolution.resolution.toBigDecimal()).toDouble()
        else
            1.0

        if (constantType != null && constants::class != constantType.type) {
            println("Can not load configuration. Constants for mechanism '${callRef.name}' should be '${constantType.type.simpleName}'.")
            exitProcess(1)
        }

        return MechanismParameters(constants, dt)
    }

    private fun checkDuration(): Int {
        if (duration <= 0) {
            println("Can not load configuration. Duration for mechanism '${callRef.name}' should be greater than 0.")
            exitProcess(1)
        }

        return duration
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