package domain

import org.jetbrains.research.mads.core.types.ModelObject

class DummyObject : ModelObject() {
    override val type = "dummy object"
    val forCondition = true
}