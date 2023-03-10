package org.jetbrains.research.mads.core.types


interface Signals {
    fun clone() : Signals
    fun state() : Map<String, Double>
}