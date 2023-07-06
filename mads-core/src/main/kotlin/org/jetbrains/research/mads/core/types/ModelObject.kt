package org.jetbrains.research.mads.core.types

import kotlinx.serialization.Serializable
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent
import org.jetbrains.research.mads.core.lattice.Lattice
import org.jetbrains.research.mads.core.lattice.emptyLattice
import org.jetbrains.research.mads.core.telemetry.ModelObjectSerializer
import kotlin.random.Random
import kotlin.reflect.KClass

class SpatialSignals : Signals() {
    var coordinate: Int by observable(-1)
    var volume: Double by observable(0.0)
}

object EmptyModelObject : ModelObject(-1)

@Serializable(with = ModelObjectSerializer::class)
abstract class ModelObject internal constructor(val id: Long, vararg signals: Signals) {

    constructor(vararg signals: Signals) : this(getNewID(), *signals)

    internal companion object {
        // access to modeling configuration as per class singleton
        internal var configuration: Configuration = Configuration()

        // changed objects static section
        private const val added = "added"
        private const val removed = "removed"

        // access to elapsed time
        internal var getCurrentTime: () -> Long = { 0 }

        // sequential ID
        private var syncId: Long = 0L

        @Synchronized
        private fun getNewID(): Long {
            return syncId++
        }
    }

    var type: String = ""
    var lattice: Lattice = emptyLattice
    var parent: ModelObject = EmptyModelObject
    val events: ArrayList<ModelEvent> = ArrayList()

    private val childObjects: HashSet<ModelObject> = HashSet()
    val connections: MutableMap<ConnectionType, HashSet<ModelObject>> = mutableMapOf()
    val signals: MutableMap<KClass<out Signals>, Signals> = mutableMapOf()

    private val operatedChildren: MutableMap<ModelObject, String> = mutableMapOf()

    var coordinate: Int
        get() {
            val spatialSignals = signals[SpatialSignals::class] as SpatialSignals
            return spatialSignals.coordinate
        }
        set(value) {
            val spatialSignals = signals[SpatialSignals::class] as SpatialSignals
            spatialSignals.coordinate = value
        }

    var volume: Double
        get() {
            val spatialSignals = signals[SpatialSignals::class] as SpatialSignals
            return spatialSignals.volume
        }
        set(value) {
            val spatialSignals = signals[SpatialSignals::class] as SpatialSignals
            spatialSignals.volume = value
        }

    init {
        this.signals[SpatialSignals::class] = SpatialSignals()
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

    fun currentTime(): Long {
        return getCurrentTime()
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

    fun createResponse(conflict: Conflict = noConflict, applyFn: () -> Unit): Response {
        return Response(this, conflict, applyFn)
    }

    fun createEmptyResponse(): Response {
        return Response(this, noConflict) { }
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

    internal fun getChangedObjects(): Map<Long, List<String>> {
        val result = operatedChildren.map { (key, value) ->
            key.id to listOf(key.type, value)
        }.toMap()
        operatedChildren.clear()

        return result
    }

    internal fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }

    protected fun createEvents(pathways: List<Pathway<out ModelObject>>) {
        pathways.forEach { createEvents(it) }
    }

    private fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses.groupBy { it.conflict.resolve }
            .entries.flatMap { entry ->
                if (entry.value.isNotEmpty()) {
                    entry.key.invoke(entry.value)
                } else {
                    emptyList()
                }
            }
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

class MoveConstants(
    val movementType: MovementType,
    val directionSelection: DirectionSelection,
    val signal: String,
    val volumetricState: VolumetricState
) : MechanismConstants

fun ModelObject.move(parameters: MechanismParameters) : List<Response> {
    val moveConstants = parameters.constants as MoveConstants
    val candidate: Int = when(moveConstants.directionSelection) {
        DirectionSelection.Random ->
            this.parent.lattice.getRandomCandidate(coordinate, moveConstants.volumetricState, Random::nextInt)
        DirectionSelection.Gradient ->
            this.parent.lattice.getGradientCandidate(coordinate, moveConstants.volumetricState, moveConstants.signal, Random::nextInt)
        DirectionSelection.Antigradient ->
            this.parent.lattice.getAntigradientCandidate(coordinate, moveConstants.volumetricState, moveConstants.signal, Random::nextInt)
        DirectionSelection.Insoluble ->
            this.parent.lattice.getInsolubleCandidate(coordinate, moveConstants.volumetricState, moveConstants.signal, Random::nextInt)
    }
    val track = when(moveConstants.movementType) {
        MovementType.Shift -> this.parent.lattice.getShiftTrackCandidate(coordinate, candidate, volume, Random::nextDouble)
        MovementType.Switch -> this.parent.lattice.getSwitchTrackCandidate(coordinate, candidate, this, Random::nextDouble)
    }

    return listOf(
        this.parent.createResponse(conflict = track.createSpatialConflict()) {
            this.parent.lattice.applyTrack(track)
        }
    )
}