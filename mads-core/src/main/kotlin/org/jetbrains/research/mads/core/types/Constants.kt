package org.jetbrains.research.mads.core.types

interface ObjectConstants

interface MechanismConstants

object EmptyConstants : MechanismConstants

class DecayConstants(val zeroingLimit: Double = 0.001,
                     val decayMultiplier: Double = 0.5,) : MechanismConstants