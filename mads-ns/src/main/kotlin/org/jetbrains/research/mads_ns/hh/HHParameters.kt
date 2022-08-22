package org.jetbrains.research.mads_ns.hh

import org.jetbrains.research.mads.core.types.*

open class HHParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants
) : MechanismParameters

object HHParamsNoSave : HHParameters(SkipSaving, HHConstants)

object HHParamsSaveToFile : HHParameters(SaveToFile, HHConstants)