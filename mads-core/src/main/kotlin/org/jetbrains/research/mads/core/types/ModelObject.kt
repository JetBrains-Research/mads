package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent
import org.jetbrains.research.mads.core.types.responses.AddObjectResponse
import org.jetbrains.research.mads.core.types.responses.RemoveObjectResponse
import kotlin.reflect.KClass

object EmptyModelObject : ModelObject()

abstract class ModelObject {
    open val type = "Model Object"
    val events: ArrayList<ModelEvent> = ArrayList()
    protected val responseMapping: MutableMap<KClass<out Response>, (Response) -> List<ModelObject>> = mutableMapOf()
    var parent: ModelObject = EmptyModelObject
    val childObjects: HashSet<ModelObject> = HashSet()
    val connections: MutableMap<ConnectionType, HashSet<ModelObject>> = mutableMapOf()

    var initialized = false
        get() = field
        private set(value) {
            field = value
        }

    init {
        responseMapping[AddObjectResponse::class] = ::addObject
        responseMapping[RemoveObjectResponse::class] = ::removeObject
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
            val event = ModelEvent(mch, cnd, it.duration)
            events.add(event)
        }
    }

    internal fun createEvents(pathways: ArrayList<Pathway<out ModelObject>>) {
        if (initialized) return

        pathways.forEach { createEvents(it) }
        initialized = true
    }

    internal fun applyResponses(tick: Long, responses: List<Response>): List<ModelObject> {
        return resolveConflicts(responses).mapNotNull {
            this.responseMapping[it::class]?.invoke(it.logFunction(tick, it))
        }.flatten()
    }

    internal fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }

    protected open fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }

    private fun addObject(response: Response): List<ModelObject> {
        if (response is AddObjectResponse) {
            println(response.response)
            response.addedObject.parent = this
            childObjects.add(response.addedObject)
            return arrayListOf(response.sourceObject, response.addedObject)
        }

        return arrayListOf()
    }

    private fun removeObject(response: Response): List<ModelObject> {
        if (response is RemoveObjectResponse) {
            println(response.response)
            childObjects.add(response.removedObject)
        }
        return arrayListOf()
    }
}

fun <T> Sequence<T>.selectRecursive(recursiveSelector: T.() -> Sequence<T>): Sequence<T> = flatMap {
    sequence {
        yield(it)
        yieldAll(it.recursiveSelector().selectRecursive(recursiveSelector))
    }
}