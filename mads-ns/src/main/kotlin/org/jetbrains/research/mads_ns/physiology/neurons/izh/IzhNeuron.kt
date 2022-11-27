package org.jetbrains.research.mads_ns.physiology.neurons.izh

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalIntChangeResponse
import org.jetbrains.research.mads_ns.physiology.neurons.*
import kotlin.math.pow

open class IzhConstants(
    val a: Double = 0.02,
    val b: Double = 0.2,
    val c: Double = -65.0,
    val d: Double = 2.0,

    val V_thresh: Double = 30.0
) : Constants

object IzhConstantsRS : IzhConstants(d = 8.0)

object IzhConstantsIB : IzhConstants(d = 4.0, c = -55.0)

data class IzhSignals(
    var U: Double = 0.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

object IzhMechanisms {
    val VDynamic = Neuron::VDynamic
    val UDynamic = Neuron::UDynamic
}

class IzhNeuron(spikeThreshold: Double, vararg signals: Signals) : Neuron(spikeThreshold, *signals) {
    init {
        responseMapping[PotentialChangeResponse::class] = ::signalChangedResponse
        responseMapping[CurrentChangeResponse::class] = ::signalChangedResponse
        responseMapping[UChangeResponse::class] = ::signalChangedResponse
        responseMapping[SpikeOnChangeResponse::class] = ::signalChangedResponse
        responseMapping[SpikeOffChangeResponse::class] = ::signalChangedResponse
    }
}

data class UChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Double,
    override val updateFn: (Double) -> Unit
) : SignalDoubleChangeResponse("${sourceObject.hashCode()}, dU, ${value}\n", sourceObject, value, updateFn)

fun Neuron.VDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val izh = this.signals[IzhSignals::class] as IzhSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals
    val consts = IzhConstantsRS

    val spiked = (u.V > consts.V_thresh)
    val delta =
        if (spiked) {
            consts.c - u.V
        } else {
            0.04 * u.V.pow(2.0) + 5 * u.V + 140 - izh.U + i.I_e
        }

    return arrayListOf(
        PotentialChangeResponse(this, delta) { u.V += it }
    )
}

fun Neuron.UDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val izh = this.signals[IzhSignals::class] as IzhSignals
    val consts = IzhConstantsRS

    val spiked = (u.V > consts.V_thresh)
    val delta =
        if (spiked) {
            consts.d
        } else {
            consts.a * (consts.b * u.V - izh.U)
        }

    return arrayListOf(
        UChangeResponse(this, delta) { izh.U += it }
    )
}