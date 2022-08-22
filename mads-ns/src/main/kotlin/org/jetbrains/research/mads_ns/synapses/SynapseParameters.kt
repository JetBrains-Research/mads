package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.*

open class SynapseParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants
) : MechanismParameters

object SynapseParamsNoSave : SynapseParameters(SkipSaving, EmptyConstants)

object SynapseParamsSaveToFile : SynapseParameters(SaveToFile, SynapseConstants)