package org.jetbrains.research.mads.core.types

fun <MO: ModelObject> applyObjectToCondition(condition: (MO) -> Boolean, obj: MO) : () -> Boolean {
    return fun (): Boolean {
        return condition(obj)
    }
}