package org.jetbrains.research.mads_ns.physiology.neurons.lif

import org.jetbrains.research.mads.core.types.*

open class LIFParameters(
        override val savingParameters: SavingParameters,
        override val constants: Constants
) : MechanismParameters

object LIFParamsNoSave : LIFParameters(SkipSaving, LIFConstants)

object LIFParamsSaveToFile : LIFParameters(SaveToFile, LIFConstants)