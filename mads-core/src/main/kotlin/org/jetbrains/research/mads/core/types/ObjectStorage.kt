package org.jetbrains.research.mads.core.types

interface ObjectStorage {
    fun addObject(obj : ModelObject)
    fun addConnection(connection: Connection)
    fun removeObject(obj: ModelObject)
    fun removeConnection(connection: Connection)

    fun getObjects(): Array<ModelObject>
}