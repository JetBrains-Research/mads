package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.desd.ModelEvent
import kotlin.reflect.KClass

abstract class ModelObject(val storage: ObjectStorage) {
    open val type = "Model Object"
    val events : ArrayList<ModelEvent> = ArrayList()
    protected val responseMapping: MutableMap<KClass<out Response>, (Response) -> Array<ModelObject>> = mutableMapOf()

    init {
        responseMapping[AddObjectResponse::class] = ::addObject
        responseMapping[RemoveObjectResponse::class] = ::removeObject
    }

    @Suppress("UNCHECKED_CAST")
    fun <MO: ModelObject> createEvents(pathway: Pathway<MO>) {
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

    fun applyResponses(responses : List<Response>): Array<ModelObject> {
        return responses.mapNotNull { this.responseMapping[it::class]?.invoke(it) }
            .toTypedArray().flatten().toTypedArray()
    }

    fun checkConditions() {
        events.forEach { if (it.checkCondition()) it.prepareEvent() else it.disruptEvent() }
    }

    private fun addObject(response: Response): Array<ModelObject> {
        if (response is AddObjectResponse) {
            println(response.response)
            storage.addObject(response.addedObject)
            return arrayOf(response.sourceObject, response.addedObject)
        }

        return arrayOf()
    }

    private fun removeObject(response: Response): Array<ModelObject> {
        if (response is RemoveObjectResponse) {
            println(response.response)
            storage.addObject(response.removedObject)
        }
        return arrayOf()
    }
}