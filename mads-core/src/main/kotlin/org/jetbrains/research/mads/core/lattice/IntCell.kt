package org.jetbrains.research.mads.core.lattice

import com.google.common.util.concurrent.AtomicDouble
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.concurrent.ConcurrentHashMap

internal class IntCell {
    private val objects: ConcurrentHashMap<ModelObject, AtomicDouble> = ConcurrentHashMap()
    val occupiedVolume: AtomicDouble = AtomicDouble(0.0)

    fun putObject(obj: ModelObject, volume: Double) {
        objects[obj] = AtomicDouble(volume)
        occupiedVolume.addAndGet(volume)
    }

    fun removeObject(obj: ModelObject): Double {
        val volume = objects.remove(obj)!!.get()
        occupiedVolume.addAndGet(-volume)
        return volume
    }

    fun getObjects(): List<ModelObject> {
        return objects.map { it.key }.toList()
    }

    fun getMovingCandidates(volume: Double): List<ModelObject> {
        val cumulativeVolume = doubleArrayOf(0.0)
        return objects.entries
            .sortedBy { it.value.get() }
            .filter {
                it.value.get().let {
                    cumulativeVolume[0] += it; cumulativeVolume[0]
                } <= volume && cumulativeVolume[0] > 0.0
            }
            .map { it.key }
            .toList()
    }

    fun getSumVolume(objects: List<ModelObject>): Double {
        return objects.sumOf { o: ModelObject -> this.objects[o]!!.get() }
    }
}