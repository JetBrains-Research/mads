package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.second
import kotlin.reflect.KClass

class Configuration {
    private val objPathways: HashMap<KClass<out ModelObject>, ArrayList<Pathway<out ModelObject>>> = HashMap()
    private val errors: ArrayList<String> = ArrayList()
    var timeResolution: Double = second

    fun addPathway(pathway: Pathway<out ModelObject>) {
        if (!pathway.checkResolution(timeResolution)) {
            errors.add("Pathway ${pathway.type.simpleName} time resolution is smaller than the resolution of configuration")
            return
        }

        if (errors.size == 0) {
            if (objPathways.containsKey(pathway.type))
                objPathways[pathway.type]!!.add(pathway)
            else
                objPathways[pathway.type] = arrayListOf(pathway)
        }
    }

    fun createEvents(obj: ModelObject) {
        objPathways[obj::class]?.let { obj.createEvents(it) }
    }

    fun hasErrors() :Boolean {
        return errors.size > 0
    }

    fun errors() : List<String> {
        return errors
    }
}

fun configure(init: Configuration.() -> Unit): Configuration {
    val configuration = Configuration()
    configuration.init()
    return configuration
}