package org.jetbrains.research.mads.core.types

class Connection(id: Long,
                 private val objectLeft: ModelObject,
                 private val objectRight: ModelObject) : ModelObject(id) {
//    val objectLeft = objectLeft
//    val objectRight = objectRight

    public fun getPair(id: Long) : ModelObject?
    {
        if(id == objectLeft.id)
        {
            return objectLeft
        }
        else if(id == objectRight.id)
        {
            return objectRight
        }

        return null
    }

}