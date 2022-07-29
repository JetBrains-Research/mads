package domain

import org.jetbrains.research.mads.core.types.ModelObject

open class SimpleObject : ModelObject() {
    override val type = "simple object"
    val forCondition = true
}