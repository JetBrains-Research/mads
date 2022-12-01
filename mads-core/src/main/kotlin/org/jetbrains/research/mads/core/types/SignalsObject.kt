package org.jetbrains.research.mads.core.types

import kotlin.reflect.KClass

abstract class SignalsObject(vararg signals: Signals) : ModelObject() {
    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()

    init {
        signals.forEach { this.signals[it::class] = it }
    }
}