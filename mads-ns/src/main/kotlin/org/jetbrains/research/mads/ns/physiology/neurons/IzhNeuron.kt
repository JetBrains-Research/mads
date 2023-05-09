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
    val Dynamic = IzhNeuron::Dynamic
    val VDynamic = IzhNeuron::VDynamic
    val UDynamic = IzhNeuron::UDynamic
}

class IzhNeuron(val izhType: IzhConstants = IzhRS, vararg signals: Signals) : Neuron(izhType.V_thresh, IzhSignals(), *signals)

@TimeResolution(resolution = millisecond)
fun IzhNeuron.Dynamic(params: MechanismParameters): List<Response> {
    val potentialSignals = this.signals[PotentialSignals::class] as PotentialSignals
    val izhSignals = this.signals[IzhSignals::class] as IzhSignals
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals

    if (potentialSignals.V > izhType.V_thresh) {
        val deltaV = izhType.c - potentialSignals.V
        val deltaU = izhType.d
        return listOf(
            this.createResponse {
                potentialSignals.V += deltaV
                izhSignals.U += deltaU
                spikesSignals.spiked = true
                spikesSignals.spikeCounterTemp += 1
            }
        )
    } else {
        val deltaV = params.dt * (0.04 * potentialSignals.V.pow(2.0) + 5 * potentialSignals.V + 140 - izhSignals.U + currentSignals.I_e * izhType.k)
        val deltaU = params.dt * (izhType.a * (izhType.b * potentialSignals.V - izhSignals.U))
        return listOf(
            this.createResponse {
                potentialSignals.V += deltaV
                izhSignals.U += deltaU
            }
        )
    }
}

@TimeResolution(resolution = millisecond)
fun IzhNeuron.VDynamic(params: MechanismParameters): List<Response> {
    val potentialSignals = this.signals[PotentialSignals::class] as PotentialSignals
    val izhSignals = this.signals[IzhSignals::class] as IzhSignals
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals

    val spiked = (potentialSignals.V > izhType.V_thresh)
    val delta = if (spiked) izhType.c - potentialSignals.V
                else params.dt * (0.04 * potentialSignals.V.pow(2.0) + 5 * potentialSignals.V + 140 - izhSignals.U + currentSignals.I_e * izhType.k)

    return listOf(
        this.createResponse {
            potentialSignals.V += delta
        }
    )
}

@TimeResolution(resolution = millisecond)
fun IzhNeuron.UDynamic(params: MechanismParameters): List<Response> {
    val potentialSignals = this.signals[PotentialSignals::class] as PotentialSignals
    val izhSignals = this.signals[IzhSignals::class] as IzhSignals

    val spiked = (potentialSignals.V > izhType.V_thresh)
    val delta = if (spiked) izhType.d
                else params.dt * (izhType.a * (izhType.b * potentialSignals.V - izhSignals.U))

    return listOf(
        this.createResponse {
            izhSignals.U += delta
        }
    )
}