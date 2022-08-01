package domain

import org.jetbrains.research.mads.core.types.ModelObject

open class SimpleObject (id: Long): ModelObject(id) {
    override val type = "simple object"
    val forCondition = true
}