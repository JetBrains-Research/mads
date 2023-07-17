package org.jetbrains.research.mads.core.lattice

import com.google.common.util.concurrent.AtomicDouble
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.concurrent.ConcurrentHashMap

internal class Cell {
    private val objects: MutableSet<ModelObject> = ConcurrentHashMap.newKeySet()
    val occupiedVolume: AtomicDouble = AtomicDouble(0.0)

    fun putObject(obj: ModelObject) {
        objects.add(obj)
        occupiedVolume.addAndGet(obj.volume)
    }

    fun removeObject(obj: ModelObject) {
        if (objects.remove(obj))
            occupiedVolume.addAndGet(-obj.volume)
    }

    fun getObjects(): List<ModelObject> {
        return objects.toList()
    }

    fun getMovingCandidates(volume: Double): List<ModelObject> {
        val cumulativeVolume = doubleArrayOf(0.0)
        return objects
            .sortedBy { it.volume }
            .filter { entry ->
                entry.volume.let {
                    cumulativeVolume[0] += it; cumulativeVolume[0]
                } <= volume && cumulativeVolume[0] > 0.0
            }
            .map { it }
            .toList()
    }

    fun getSumVolume(objects: List<ModelObject>): Double {
        return objects.sumOf { it.volume }
    }
}