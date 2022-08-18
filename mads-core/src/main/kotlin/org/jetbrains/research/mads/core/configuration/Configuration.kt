package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.ModelObject
import kotlin.reflect.KClass

class Configuration {
    private val objPathways: HashMap<KClass<out ModelObject>, ArrayList<Pathway<out ModelObject>>> = HashMap()

    fun addPathway(pathway: Pathway<out ModelObject>) {
        if (objPathways.containsKey(pathway.type))
            objPathways[pathway.type]!!.add(pathway)
        else
            objPathways[pathway.type] = arrayListOf(pathway)
    }

    fun createEvents(obj: ModelObject) {
        objPathways[obj::class]?.forEach {
            obj.createEvents(it)
        }
    }
}

fun configure(init: Configuration.() -> Unit): Configuration {
    val configuration = Configuration()
    configuration.init()
    return configuration
}