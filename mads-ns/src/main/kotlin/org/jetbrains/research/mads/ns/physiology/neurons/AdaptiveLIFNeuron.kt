package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*

object AdaptiveLIFMechanisms {
    val VDynamic = AdaptiveLIFNeuron::VDynamic
    val ThetaSpike = AdaptiveLIFNeuron::thetaSpike
    val ThetaDecay = AdaptiveLIFNeuron::thetaDecay
}

class AdaptiveSignals : Signals() {
    var theta: Double by observable(0.0)
    var refracTimer: Int by observable(100500)
    var icond: Double by observable(0.0)
}

object AdaptiveLIFConstants : ObjectConstants {
    const val tau_mem = 20.0
    const val V_reset = -65.0
    const val V_rest = -65.0
    const val V_thresh = -52.0
    const val Rm = 1.25
}

class AdaptiveLIFNeuron(
    val adaptiveThreshold: Boolean = false,
    val isInhibitory: Boolean = false,
    val hasRefracTimer: Boolean = false,
    val weightNormalizationEnabled: Boolean = false,
    vararg signals: Signals
) : Neuron(0.0, AdaptiveSignals(), *signals)

@TimeResolution(resolution = millisecond)
fun AdaptiveLIFNeuron.VDynamic(params: MechanismParameters): List<Response> {
    val s = this.signals[PotentialSignals::class] as PotentialSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals
    val a = this.signals[AdaptiveSignals::class] as AdaptiveSignals

    var spiked = (s.V > (AdaptiveLIFConstants.V_thresh + a.theta))

    if (this.hasRefracTimer) {
        spiked = spiked && (a.refracTimer > 500)
    }

    val icond = -s.V * i.I_e
    val norm_coeff = if (isInhibitory) 0.1 else 0.01

    val delta =
        if (a.refracTimer < 500) {
            0.0
        } else if (spiked) {
            AdaptiveLIFConstants.V_reset - s.V
        } else {
            //((v_rest_e - v) + (I_synE+I_synI) / nS) / (100*ms)
            (AdaptiveLIFConstants.V_rest - s.V + icond) * params.dt * norm_coeff
        }

    if (spiked) {
        val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
        val spikeResponses = this.spikesInSynapses()

        return listOf(
            this.createResponse {
                s.V += delta
                a.refracTimer = 0
                a.icond = icond
                spikesSignals.spiked = true
                spikesSignals.spikeCounterTemp += 1
            },
            *spikeResponses.toTypedArray()
        )
    } else {
        return listOf(
            this.createResponse {
                s.V += delta
                a.refracTimer += 1
                a.icond = icond
            }
        )
    }
}

@Suppress("UNUSED_PARAMETER")
fun AdaptiveLIFNeuron.thetaSpike(params: MechanismParameters): List<Response> {
    val adapt = this.signals[AdaptiveSignals::class] as AdaptiveSignals

    val delta = 0.05
    return arrayListOf(
        this.createResponse {
            adapt.theta += delta
        }
    )
}

@Suppress("UNUSED_PARAMETER")
fun AdaptiveLIFNeuron.thetaDecay(params: MechanismParameters): List<Response> {
    val adapt = this.signals[AdaptiveSignals::class] as AdaptiveSignals
    val delta = -adapt.theta * 0.1

    return arrayListOf(
        this.createResponse { // TODO: constant?
            adapt.theta += delta
        }
    )
}