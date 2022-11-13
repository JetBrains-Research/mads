package org.jetbrains.research.mads_ns.physiology.neurons.izh

import org.jetbrains.research.mads.core.types.*

open class IzhParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants
) : MechanismParameters

object IzhRSParamsNoSave : IzhParameters(SkipSaving, IzhConstantsRS)

object IzhRSParamsSaveToFile : IzhParameters(SaveToFile, EmptyConstants)