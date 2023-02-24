package org.jetbrains.research.mads_ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
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
    val VDynamic = IzhNeuron::VDynamic
    val UDynamic = IzhNeuron::UDynamic
}

class IzhNeuron(spikeThreshold: Double, vararg signals: Signals) : Neuron(spikeThreshold, *signals)

@TimeResolutionAnnotation(resolution = millisecond)
fun IzhNeuron.VDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val izh = this.signals[IzhSignals::class] as IzhSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals
    val consts = IzhConstantsRS

    val spiked = (u.V > consts.V_thresh)
    val delta =
        if (spiked) {
            consts.c - u.V
        } else {
            params.dt * (0.04 * u.V.pow(2.0) + 5 * u.V + 140 - izh.U + i.I_e)
        }

    return arrayListOf(
        this.createResponse("dV",delta.toString()) {
            u.V += delta
        },
        this.createResponse("VVal",u.V.toString()) { }
    )
}

@TimeResolutionAnnotation(resolution = millisecond)
fun IzhNeuron.UDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val izh = this.signals[IzhSignals::class] as IzhSignals
    val consts = IzhConstantsRS

    val spiked = (u.V > consts.V_thresh)
    val delta =
        if (spiked) {
            consts.d
        } else {
            params.dt * (consts.a * (consts.b * u.V - izh.U))
        }

    return arrayListOf(
        this.createResponse("dU",delta.toString()) {
            izh.U += delta
        }
    )
}