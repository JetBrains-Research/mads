package domain.mechanisms

import domain.Signals
import domain.SimpleParameters
import domain.objects.DynamicObject
import domain.responses.DynamicResponse
import org.jetbrains.research.mads.core.types.Response

fun DynamicObject.simpleDynamicMechanism(params: SimpleParameters) : Array<Response> {
    this.signals as DynSignals
    val delta: Double = this.signals.x + this.signals.x / 2
    return arrayOf(
        DynamicResponse("Object: " + this.type + "; Probability: " + params.probability, this, delta) { this.signals.x += it })
}

data class DynSignals(var x: Double = 10.0) : Signals