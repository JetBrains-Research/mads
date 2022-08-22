package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.Constants
import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.SavingParameters

open class SimpleParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants,
    p: Double,
) : MechanismParameters {
    val probability = p
}