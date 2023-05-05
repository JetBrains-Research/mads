package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*

object LIFConstants : ObjectConstants {
    const val tau_mem       = 20.0
    const val E_leak        = -60.0
    const val V_reset       = -70.0
    const val V_thresh      = -50.0
    const val Rm            = 10.0
}

object LIFMechanisms {
    val VDynamic = LIFNeuron::VDynamic
}

class LIFNeuron(spikeThreshold: Double, vararg signals: Signals) : Neuron(spikeThreshold, *signals)

@TimeResolution(resolution = millisecond)
fun LIFNeuron.VDynamic(params: MechanismParameters): List<Response> {
    val s = this.signals[PotentialSignals::class] as PotentialSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals

    val spiked = (s.V > LIFConstants.V_thresh)
    val delta =
        if (spiked) LIFConstants.V_reset - s.V else (LIFConstants.E_leak - s.V + (LIFConstants.Rm * i.I_e)) / LIFConstants.tau_mem * params.dt

    return arrayListOf(
        this.createResponse {
            s.V += delta
        }
    )
}