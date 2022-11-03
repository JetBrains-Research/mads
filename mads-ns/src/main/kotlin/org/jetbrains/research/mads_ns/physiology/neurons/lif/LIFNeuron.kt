package org.jetbrains.research.mads_ns.physiology.neurons.lif

import org.jetbrains.research.mads.core.types.Constants
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.SpikesSignals

class LIFNeuron : Neuron(SpikesSignals(), CurrentSignals(), LIFSignals())

object LIFConstants : Constants {
    const val tau_mem       = 20.0
    const val E_leak        = -60.0
    const val V_reset       = -70.0
    const val V_thresh      = -50.0
    const val Rm            = 10.0

    // dt
    const val dt = 0.01
}

data class LIFSignals(
    var V: Double = -65.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

object LIFMechanisms {
    val VDynamic = LIFNeuron::VDynamic
}

fun LIFNeuron.VDynamic(params: LIFParameters): List<Response> {
    val s = this.signals[LIFSignals::class] as LIFSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals

    val spiked = (s.V > LIFConstants.V_thresh)
    val delta =
        if (spiked) LIFConstants.V_reset - s.V else (LIFConstants.E_leak - s.V + (LIFConstants.Rm * i.I_e)) / LIFConstants.tau_mem * LIFConstants.dt

    println(s.V)

    val responseString = "${this.hashCode()}, dV, ${delta}\n"
    return arrayListOf(
        SignalDoubleChangeResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta
        ) { s.V += it }
    )
}