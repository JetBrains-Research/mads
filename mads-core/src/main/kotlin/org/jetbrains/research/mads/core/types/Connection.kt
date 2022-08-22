package org.jetbrains.research.mads.core.types

open class Connection(
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

    fun getLeftObject(): ModelObject {
        return this.objectLeft
    }

    fun getRightObject(): ModelObject {
        return this.objectRight
    }
}