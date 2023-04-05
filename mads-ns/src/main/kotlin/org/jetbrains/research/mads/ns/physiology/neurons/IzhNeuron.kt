package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
import kotlin.math.pow

open class IzhConstants(
    val a: Double = 0.02,       // timescale of recovery variable
    val b: Double = 0.2,        // sensitivity of recovery variable
    val c: Double = -65.0,      // after-spike reset value of membrane potential
    val d: Double = 2.0,        // after-spike reset value of recovery variable
    val k: Double = 1.0,        // scaling factor of the membrane potential

    val V_thresh: Double = 30.0 // membrane potential threshold for spike initiation
) : ObjectConstants

// main excitatory type: Regular Spiking (RS)
object IzhRS : IzhConstants(d = 8.0)

// excitatory type: Intrinsically Bursting (IB)
object IzhIB : IzhConstants(d = 4.0, c = -55.0, k = 0.7)

// excitatory type: Chattering (CH)
object IzhCH : IzhConstants(d = 2.0, c = -50.0)

// inhibitory type: Fast spiking (FS)
object IzhFS : IzhConstants(a = 0.1)

// inhibitory type: Low-threshold spiking (LTS)
object IzhLTS : IzhConstants(b = 0.25)

// cortex input: Thalamo-cortical (TC)
object IzhTC : IzhConstants(b = 0.25, d = 0.05)

// resonator (RZ)
object IzhRZ : IzhConstants(a = 0.01, b = 0.26, d = 0.0)

class IzhSignals : Signals() {
    var U: Double by observable(0.0)
}

object IzhMechanisms {
    val VDynamic = IzhNeuron::VDynamic
    val UDynamic = IzhNeuron::UDynamic
}

class IzhNeuron(val izhType: IzhConstants = IzhRS, vararg signals: Signals) : Neuron(izhType.V_thresh, IzhSignals(), *signals)

@TimeResolutionAnnotation(resolution = millisecond)
fun IzhNeuron.VDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val izh = this.signals[IzhSignals::class] as IzhSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals
    val consts = izhType

    val spiked = (u.V > consts.V_thresh)
    val delta =
        if (spiked) {
            consts.c - u.V
        } else {
            params.dt * (0.04 * u.V.pow(2.0) + 5 * u.V + 140 - izh.U + i.I_e * izhType.k)
//            (0.04 * u.V.pow(2.0) + 5 * u.V + 140 - izh.U + i.I_e * izhType.k)
        }

    return arrayListOf(
        this.createResponse {
            u.V += delta
        }
    )
}

@TimeResolutionAnnotation(resolution = millisecond)
fun IzhNeuron.UDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val izh = this.signals[IzhSignals::class] as IzhSignals
    val consts = izhType

    val spiked = (u.V > consts.V_thresh)
    val delta =
        if (spiked) {
            consts.d
        } else {
            params.dt * (consts.a * (consts.b * u.V - izh.U))
//            params.dt * (consts.a * (consts.b * u.V - izh.U))
        }

    return arrayListOf(
        this.createResponse {
            izh.U += delta
        }
    )
}