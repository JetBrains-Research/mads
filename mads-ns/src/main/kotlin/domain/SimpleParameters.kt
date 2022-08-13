package domain

import org.jetbrains.research.mads.core.types.MechanismParameters

open class SimpleParameters(p: Double, override val logResponse: Boolean) : MechanismParameters {
    val probability = p
}