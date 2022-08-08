package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.types.responses.DynamicResponse

open class SignalsObject(open val signals: Signals) : ModelObject() {
    override val type = "physical object"

//    val history = mutableListOf<Signals>()

    init {
        responseMapping[DynamicResponse::class] = ::dynamicResponse
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