package domain

import org.jetbrains.research.mads.core.types.MechanismParameters

open class SimpleParameters(p: Double) : MechanismParameters {
    val probability = p
}