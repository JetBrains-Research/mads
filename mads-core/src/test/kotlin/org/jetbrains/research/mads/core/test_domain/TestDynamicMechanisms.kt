package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.responses.SignalChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse

fun DynamicObject.simpleDynamicMechanism(params: SimpleParameters): List<Response> {
    val s = this.signals[DynSignals::class] as DynSignals
    val delta: Double = s.x + s.x / 2
    return arrayListOf(
        SignalDoubleChangeResponse(
            "Object: " + this.type + "; Probability: " + params.probability,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta
        ) { s.x += it })
}

data class DynSignals(var x: Double = 10.0) : Signals
{
    override fun clone(): Signals {
        return this.copy()
    }
}