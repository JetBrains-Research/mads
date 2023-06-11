package org.jetbrains.research.mads.core.lattice

import com.google.common.util.concurrent.AtomicDoubleArray
import org.jetbrains.research.mads.core.types.ModelObject

interface Lattice {
    fun getObjects(coordinate: Int): List<ModelObject>
    fun addObject(obj: ModelObject, coordinate: Int, volume: Double): ModelObject
    fun removeObject(obj: ModelObject, coordinate: Int)
    fun moveObject(obj: ModelObject, srcCoordinate: Int, dstCoordinate: Int): ModelObject
    fun updateConcentration(coordinate: Int, signal: Int, value: Double)
    fun calcDiffusion(signal: Int, diffusionRate: Double): AtomicDoubleArray
    fun updateDiffusion(signal: Int, diff: AtomicDoubleArray): List<ModelObject>
    fun getConcentration(signal: Int, coordinate: Int): Double
}

enum class VolumetricState {
    Any, Free
}