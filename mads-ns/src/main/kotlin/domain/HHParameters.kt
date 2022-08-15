package domain

import org.jetbrains.research.mads.core.types.Constants
import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.SavingParameters

data class HHParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants
) : MechanismParameters