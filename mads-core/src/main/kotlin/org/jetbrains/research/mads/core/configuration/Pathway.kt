package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.second
import kotlin.reflect.KClass

class Pathway<MO : ModelObject>(val type: KClass<MO>) {
    val configuredMechanisms = ArrayList<ConfiguredMechanism<MO>>()
    var timeResolution: Double = second
    private var timeResolutionCoefficient: Int = 1

    fun mechanism(
        mechanism: (MO, MechanismParameters) -> List<Response>,
        lambda: ConfiguredMechanismBuilder<MO>.() -> Unit
    ) {
        configuredMechanisms.add(ConfiguredMechanismBuilder(mechanism, timeResolution).apply(lambda).build())
    }

    fun checkResolution(globalResolution: Double) : Boolean {
        timeResolutionCoefficient = (timeResolution / globalResolution).toInt()
        return globalResolution <= timeResolution
    }

    fun normalizeDuration(duration: Int): Int {
        return duration * timeResolutionCoefficient
    }

    fun normalizeDelay(delay: (MO) -> Int, obj: MO): () -> Int {
        return { delay(obj) * timeResolutionCoefficient }
    }

    companion object {
        inline operator fun <reified MO : ModelObject> invoke(): Pathway<MO> = Pathway(MO::class)
    }
}

inline fun <reified MO : ModelObject> pathway(init: Pathway<MO>.() -> Unit): Pathway<MO> {
    val pathway: Pathway<MO> = Pathway()
    pathway.init()
    return pathway
}