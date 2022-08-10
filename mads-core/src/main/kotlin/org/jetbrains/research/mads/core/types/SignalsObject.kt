package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import kotlin.reflect.KClass

open class SignalsObject(vararg signals: Signals) : ModelObject() {
    override val type = "physical object"
    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()

//    val history = mutableListOf<Signals>()

    init {
        responseMapping[DynamicResponse::class] = ::dynamicResponse
        signals.forEach { this.signals[it::class] = it }
    }

    private fun dynamicResponse(response: Response): List<ModelObject> {
        if (response is DynamicResponse) {
            response.updateFn(response.delta)

//            if(this.signals is HHSignals) {
//                var signalsCopy = HHSignals((this.signals as HHSignals).I, (this.signals as HHSignals).V, (this.signals as HHSignals).N, (this.signals as HHSignals).M, (this.signals as HHSignals).H)
//                history.add(signalsCopy)
//            }
        }

        return arrayListOf(this)
    }
}