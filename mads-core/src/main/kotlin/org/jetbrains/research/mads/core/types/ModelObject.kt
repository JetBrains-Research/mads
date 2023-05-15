package org.jetbrains.research.mads.core.types

import kotlinx.serialization.Serializable
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent
import org.jetbrains.research.mads.core.telemetry.ModelObjectSerializer
import kotlin.reflect.KClass

object EmptyModelObject : ModelObject()

@Serializable(with = ModelObjectSerializer::class)
abstract class ModelObject(vararg signals: Signals) {

    internal companion object {
        // access to modeling configuration as per class singleton
        internal var configuration: Configuration = Configuration()

        // changed objects static section
        private const val added = "added"
        private const val removed = "removed"
    }

    var type: String = ""
    var parent: ModelObject = EmptyModelObject
    val events: ArrayList<ModelEvent> = ArrayList()

    private val childObjects: HashSet<ModelObject> = HashSet()
    val connections: MutableMap<ConnectionType, HashSet<ModelObject>> = mutableMapOf()
    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()

    private val operatedChildren: MutableMap<ModelObject, String> = mutableMapOf()

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

    fun addObject(addedObject: ModelObject): List<ModelObject> {
        addedObject.parent = this
        addedObject.createEvents(configuration.getPathways(addedObject::class))
        childObjects.add(addedObject)
        operatedChildren[addedObject] = added
        return arrayListOf(this, addedObject)
    }

    fun removeObject(removedObject: ModelObject): List<ModelObject> {
        removedObject.events.forEach { it.disruptEvent() }
        removedObject.events.clear()
        childObjects.remove(removedObject)
        operatedChildren[removedObject] = removed
        return arrayListOf(this)
    }

    fun addConnection(connection: ModelObject, connectionType: ConnectionType): List<ModelObject> {
        if (!connections.containsKey(connectionType)) {
            connections[connectionType] = HashSet()
        }

        connections[connectionType]!!.add(connection)
        return arrayListOf(this, connection)
    }

    fun removeConnection(connection: ModelObject, connectionType: ConnectionType): List<ModelObject> {
        connections[connectionType]!!.remove(connection)
        return arrayListOf(this)
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

    internal fun getChangedObjects() : Map<String, List<String>> {
        val result = operatedChildren.map { (key, value) ->
            key.hashCode().toString() to listOf(key.type, value) }
            .toMap()
        operatedChildren.clear()

        return result
    }

    internal fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }

    protected open fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }

    protected fun createEvents(pathways: List<Pathway<out ModelObject>>) {
        pathways.forEach { createEvents(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <MO : ModelObject> createEvents(pathway: Pathway<MO>) {
        this as MO
        pathway.configuredMechanisms.forEach {
            val mch = applyObjectToMechanism(it.mechanism, this)
            val cnd = applyObjectToCondition(it.condition, this)
            val duration = pathway.normalizeDuration(it.duration)
            val delay = pathway.normalizeDelay(it.delay, this)
            val event = ModelEvent(mch, cnd, duration, delay)
            events.add(event)
        }
    }
}

fun <T> Sequence<T>.selectRecursive(recursiveSelector: T.() -> Sequence<T>): Sequence<T> = flatMap {
    sequence {
        yield(it)
        yieldAll(it.recursiveSelector().selectRecursive(recursiveSelector))
    }
}