package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import kotlin.reflect.KClass

open class SignalsObject(vararg signals: Signals) : ModelObject() {
    override val type = "physical object"
    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()
//    val history: MutableMap<KClass<out Signals>, MutableList<Signals>> = mutableMapOf()

    init {
        responseMapping[DynamicResponse::class] = ::dynamicResponse
        signals.forEach { this.signals[it::class] = it }
//        signals.forEach { this.history[it::class] = mutableListOf() }
    }

    private fun dynamicResponse(response: Response): List<ModelObject> {
        if (response is DynamicResponse) {
            response.updateFn(response.delta)

//            this.signals.forEach {
//                this.history[it.key]?.add(it.value.clone())
//            }
        }

        return arrayListOf(this)
    }
}