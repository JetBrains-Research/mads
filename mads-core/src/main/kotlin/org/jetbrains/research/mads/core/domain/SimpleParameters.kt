package org.jetbrains.research.mads.core.domain

import org.jetbrains.research.mads.core.types.MechanismParameters

class SimpleParameters(p: Double) : MechanismParameters(10) {
    val probability = p
}