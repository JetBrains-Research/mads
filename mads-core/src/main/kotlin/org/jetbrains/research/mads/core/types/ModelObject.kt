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

    init {
        responseMapping[AddObjectResponse::class] = ::addObject
        responseMapping[RemoveObjectResponse::class] = ::removeObject
    }

    @Suppress("UNCHECKED_CAST")
    fun <MO : ModelObject> createEvents(pathway: Pathway<MO>) {
        if (events.size > 0)
            return

        this as MO
        pathway.mocRecords.forEach {
            val mch = applyObjectToMechanism(it.mechanism, this)
            val cnd = applyObjectToCondition(it.condition, this)
            val event = ModelEvent(mch, cnd, it.duration)
            events.add(event)
        }
    }

    fun applyResponses(responses: List<Response>): List<ModelObject> {
        return resolveConflicts(responses).mapNotNull {
            this.responseMapping[it::class]?.invoke(it.log(0, it)) }.flatten()
    }

    protected open fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }

    fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }

    fun getChildObjects(): Array<ModelObject> {
        return childObjects.toTypedArray()
    }

    fun recursivelyGetChildObjects(): List<ModelObject> {
        return childObjects.asSequence()
            .selectRecursive { getChildObjects().asSequence() }
            .toList()
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