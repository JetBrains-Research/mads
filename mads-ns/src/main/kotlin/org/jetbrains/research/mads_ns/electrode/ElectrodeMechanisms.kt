package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentChangeResponse
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals

object ElectrodeMechanisms {
    val PulseDynamic = Electrode::PulseDynamic
}

fun Electrode.PulseDynamic(params: ElectrodeParameters): List<Response> {
    val s = this.signals[CurrentSignals::class] as CurrentSignals

    var I = 0.0
    if (s.I_e == 0.0 && rnd.nextDouble() < params.pulseProbability) {
        I = params.pulseValue
    }

    val delta = I - s.I_e

    val responseString = "${this.hashCode()}, I, ${I}\n"
    return arrayListOf(
        CurrentChangeResponse(this, delta) { s.I_e += it }
    )
}