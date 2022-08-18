package domain

import org.jetbrains.research.mads.core.types.*

open class HHParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants
) : MechanismParameters

object HHParamsNoSave : HHParameters(SkipSaving, HHConstants)

object HHParamsSaveToFile : HHParameters(SaveToFile, HHConstants)