package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
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

    val responseString = "${this.hashCode()}, I, ${I}\n"
    return arrayListOf(
        SignalDoubleChangeResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            I,
            this::updateI
        )
    )
}