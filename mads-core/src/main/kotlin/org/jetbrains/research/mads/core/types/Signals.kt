package org.jetbrains.research.mads.core.types

import kotlinx.serialization.Serializable

//@Serializable
interface Signals {
    fun clone() : Signals
}