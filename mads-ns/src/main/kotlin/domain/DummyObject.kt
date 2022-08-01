package domain

import org.jetbrains.research.mads.core.types.ModelObject

class DummyObject(id: Long) : ModelObject(id) {
    override val type = "dummy object"
    val forCondition = true
}