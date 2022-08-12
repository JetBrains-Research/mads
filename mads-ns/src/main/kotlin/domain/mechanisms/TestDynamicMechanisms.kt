package domain.mechanisms

import org.jetbrains.research.mads.core.types.Signals
import domain.SimpleParameters
import domain.objects.DynamicObject
import org.jetbrains.research.mads.core.telemetry.DataBroker
import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import org.jetbrains.research.mads.core.types.Response

fun DynamicObject.simpleDynamicMechanism(params: SimpleParameters) : List<Response> {
    this.signals as DynSignals
    val delta: Double = this.signals.x + this.signals.x / 2
    return arrayListOf(
        DynamicResponse("Object: " + this.type + "; Probability: " + params.probability, this, DataBroker.INSTANCE::logResponse, delta) { this.signals.x += it })
}

data class DynSignals(var x: Double = 10.0) : Signals