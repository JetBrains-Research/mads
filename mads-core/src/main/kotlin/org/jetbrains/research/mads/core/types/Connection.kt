package org.jetbrains.research.mads.core.types

class Connection(
    private val objectLeft: ModelObject,
    private val objectRight: ModelObject
) {

    fun getPair(obj: ModelObject): ModelObject? {
        return when (obj) {
            objectLeft -> objectRight
            objectRight -> objectLeft
            // TODO: @vlad0922 I prefer to change null to some blank empty object
            else -> null
        }
    }
}