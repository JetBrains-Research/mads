package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalIntChangeResponse
import kotlin.reflect.KClass

open class SignalsObject(vararg signals: Signals) : ModelObject() {
    override val type = "physical object"
    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()

    init {
        responseMapping[SignalBooleanChangeResponse::class] = ::signalChangedResponse
        responseMapping[SignalIntChangeResponse::class] = ::signalChangedResponse
        responseMapping[SignalDoubleChangeResponse::class] = ::signalChangedResponse
        signals.forEach { this.signals[it::class] = it }
    }

    private fun signalChangedResponse(response: Response): List<ModelObject> {
        when (response::class) {
            SignalBooleanChangeResponse::class -> {
                response as SignalBooleanChangeResponse
                response.updateFn(response.value)
            }

            SignalIntChangeResponse::class -> {
                response as SignalIntChangeResponse
                response.updateFn(response.value)
            }

            SignalDoubleChangeResponse::class -> {
                response as SignalDoubleChangeResponse
                response.updateFn(response.value)
            }

            else -> println("Signal update type not supported")
        }

        return arrayListOf(this)
    }
}