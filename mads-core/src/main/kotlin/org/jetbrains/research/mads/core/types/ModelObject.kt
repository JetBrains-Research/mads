package org.jetbrains.research.mads.core.types

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent
import org.jetbrains.research.mads.core.telemetry.ModelObjectSerizalizer

object EmptyModelObject : ModelObject()

@Serializable(with= ModelObjectSerizalizer::class)
abstract class ModelObject(vararg signals: Signals) {

    var type: String = ""
    var parent: ModelObject = EmptyModelObject
    val events: ArrayList<ModelEvent> = ArrayList()

    val childObjects: HashSet<ModelObject> = HashSet()
    val connections: MutableMap<ConnectionType, HashSet<ModelObject>> = mutableMapOf()

    var initialized = false
        private set

    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()

    init {
        signals.forEach { this.signals[it::class] = it }
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
            val event = ModelEvent(mch, cnd, it.duration * pathway.timeResolutionCoefficient)
            events.add(event)
        }
    }

    internal fun createEvents(pathways: ArrayList<Pathway<out ModelObject>>) {
        pathways.forEach { createEvents(it) }
        initialized = true
    }

    fun createResponse(applyFn: () -> Unit) : Response {
        return Response(this, applyFn)
    }

    internal fun applyResponses(responses: List<Response>): List<ModelObject> {
        return resolveConflicts(responses).map {
            it.applyFn()
            it.sourceObject
        }
    }

    internal fun getChangedSignals(): Map<String, String> {
        return signals.flatMap { (key, value) ->
            value.getUpdatedProperties().map { (innerKey, innerValue) ->
                "${key.simpleName}.$innerKey" to innerValue
            }
        }.toMap()
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