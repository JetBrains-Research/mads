package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals

fun DynamicObject.simpleDynamicMechanism(params: SimpleParameters): List<Response> {
    val s = this.signals[DynSignals::class] as DynSignals
    val delta: Double = s.x + s.x / 2
    return arrayListOf(
        this.createResponse("dx",delta.toString()) {
            s.x += delta
        }
    )
}

data class DynSignals(var x: Double = 10.0) : Signals
{
    override fun clone(): Signals {
        return this.copy()
    }
}