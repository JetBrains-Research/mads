package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent

object EmptyModelObject : ModelObject()

abstract class ModelObject {
    val events: ArrayList<ModelEvent> = ArrayList()
    var parent: ModelObject = EmptyModelObject
    val childObjects: HashSet<ModelObject> = HashSet()
    val connections: MutableMap<ConnectionType, HashSet<ModelObject>> = mutableMapOf()

    var initialized = false
        get() = field
        private set(value) {
            field = value
        }

    fun getChildObjects(): Array<ModelObject> {
        return childObjects.toTypedArray()
    }

    fun recursivelyGetChildObjects(): List<ModelObject> {
        return childObjects.asSequence()
            .selectRecursive { getChildObjects().asSequence() }
            .toList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <MO : ModelObject> createEvents(pathway: Pathway<MO>) {
        this as MO
        pathway.configuredMechanisms.forEach {
            val mch = applyObjectToMechanism(it.mechanism, this)
            val cnd = applyObjectToCondition(it.condition, this)
            val event = ModelEvent(mch, cnd, it.duration, it.logFn)
            events.add(event)
        }
    }

    internal fun createEvents(pathways: ArrayList<Pathway<out ModelObject>>) {
        if (initialized) return

        pathways.forEach { createEvents(it) }
        initialized = true
    }

    fun createResponse(string: String, applyFn: () -> Unit) : Response {
        return Response(this, string, applyFn)
    }

    internal fun applyResponses(currentTime: Long, responses: List<Response>): List<ModelObject> {
        return resolveConflicts(responses).map {
            it.applyFn()
            it.logFn(currentTime, it).sourceObject
        }
    }

    internal fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }

    protected open fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }

    fun addObject(addedObject: ModelObject): List<ModelObject> {
        addedObject.parent = this
        childObjects.add(addedObject)
        return arrayListOf(this, addedObject)
    }

    fun removeObject(removedObject: ModelObject): List<ModelObject> {
        childObjects.remove(removedObject)
        return arrayListOf()
    }
}

fun <T> Sequence<T>.selectRecursive(recursiveSelector: T.() -> Sequence<T>): Sequence<T> = flatMap {
    sequence {
        yield(it)
        yieldAll(it.recursiveSelector().selectRecursive(recursiveSelector))
    }
}