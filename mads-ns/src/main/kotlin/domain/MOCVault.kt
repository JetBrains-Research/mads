package domain

import org.jetbrains.research.mads.core.types.Connection
import java.util.concurrent.ConcurrentHashMap

object MOCVault {
    private val logicObjects = ConcurrentHashMap<Long, SimpleObject>()
    private val connections = ConcurrentHashMap<Long, Set<Connection>>()

    public fun getObject(id: Long): SimpleObject? {
        return logicObjects[id]
    }

    public fun getObjects(): Array<SimpleObject> {
        return logicObjects.values.toTypedArray()
    }

    public fun addObject(newObject: SimpleObject) {
        logicObjects[newObject.id] = newObject
    }

    public fun addConnection(connection: Connection)
    {

    }

    public fun removeConnection(id: Long)
    {
        
    }
}

