package org.jetbrains.research.mads.core.types

interface Response {
    val response : String
    val type : ResponseEnum

    fun applyResponse() : Array<Long>
    fun getExporterData(tick: Long) : String
    fun isConflicting() : Boolean
}