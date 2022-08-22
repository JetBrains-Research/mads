package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.*

open class ElectrodeParameters(
    override val savingParameters: SavingParameters,
    override val constants: Constants,
    val pulseProbability: Double = 0.5,
    val pulseValue: Double = 10.0
) : MechanismParameters

class ElectrodeParametersNoSave(pulseProbability: Double, pulseValue: Double) :
    ElectrodeParameters(SkipSaving, EmptyConstants, pulseProbability, pulseValue)

class ElectrodeParametersSaveToFile(pulseProbability: Double, pulseValue: Double) :
    ElectrodeParameters(SaveToFile, EmptyConstants, pulseProbability, pulseValue)