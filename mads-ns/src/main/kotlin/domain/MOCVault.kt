package domain

import org.jetbrains.research.mads.core.types.Connection
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.ObjectStorage
import java.util.concurrent.ConcurrentHashMap

class BaseObjectStorage: ObjectStorage {
    private val modelObjects = ConcurrentHashMap.newKeySet<ModelObject>()
    private val connections = ConcurrentHashMap.newKeySet<Connection>()

    override fun addObject(obj: ModelObject) {
        modelObjects.add(obj)
    }

    override fun addConnection(connection: Connection) {
        connections.add(connection)
    }

    override fun removeObject(obj: ModelObject) {
        modelObjects.remove(obj)
    }

    override fun removeConnection(connection: Connection) {
        connections.remove(connection)
    }

    override fun getObjects(): Array<ModelObject> {
        return modelObjects.toTypedArray()
    }
}

