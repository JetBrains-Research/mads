package org.jetbrains.research.mads.core.lattice

import org.jetbrains.research.mads.core.types.ModelObject
import java.util.stream.Collectors
import java.util.stream.IntStream

sealed class Track(val rolledRnd: Double) {
    val coordinates: ArrayList<Int> = ArrayList()
    private val objects: ArrayList<List<ModelObject>> = ArrayList()
    var objectsCount: Int = 0
        private set

    fun add(coordinate: Int, objs: List<ModelObject>) {
        coordinates.add(coordinate)
        objects.add(objs)
        objectsCount += objs.size
    }

    fun applyTrack(lattice: Lattice): List<ModelObject> {
        val result: ArrayList<ModelObject> = ArrayList()
        for (i in 0 until this.coordinates.size - 1) {
            for (obj in objects[i]) {
                lattice.moveObject(obj, coordinates[i], coordinates[i + 1])
                obj.coordinate = coordinates[i + 1]
                result.add(obj)
            }
        }
        return result
    }
}

class ShiftTrack(rolledRnd: Double) : Track(rolledRnd)

class SwitchTrack(rolledRnd: Double) : Track(rolledRnd)

object EmptyTrack : Track(0.0)

fun resolveCollisions(tracks: List<Track>): List<Track> {
    val tracksCount = tracks.size
    val collisionMatrix = Array(tracksCount) { IntArray(tracksCount) }
    val collisionCounter = IntArray(tracksCount)
    val wVector = IntArray(tracksCount) // weighted vector of collision count
    // for each [track * length] of track

    val intTracks = ArrayList<IntArray>(tracksCount)
    val rnd = DoubleArray(tracksCount)
    IntStream.range(0, tracks.size)
        .parallel()
        .forEach { i: Int ->
            intTracks[i] = tracks[i].coordinates.toIntArray()
            wVector[i] = tracks[i].objectsCount
            rnd[i] = tracks[i].rolledRnd
        }

    // counting collisions here in parallel by track
    IntStream.range(0, intTracks.size)
        .parallel()
        .forEach { i: Int ->
            for (j in intTracks.indices) {
                if (i == j) continue
                val collision = checkTracksCollision(intTracks[i], intTracks[j])
                collisionMatrix[i][j] = collision
                collisionCounter[i] += collision
            }
        }

    // calculating weights for each tracks
    for (i in wVector.indices) wVector[i] *= collisionCounter[i]

    // weight comparison
    IntStream.range(0, collisionMatrix.size)
        .parallel()
        .forEach { i: Int ->
            for (j in collisionMatrix.indices) {
                if (i == j)
                    continue
                if ((wVector[i] < wVector[j] || wVector[i] == wVector[j]) && rnd[i] < rnd[j])
                    collisionMatrix[i][j] = 0
            }
        }

    // filtering tracks with 0 collisions
    return IntStream.range(0, collisionMatrix.size)
        .parallel()
        .filter { i: Int ->
            IntStream.of(*collisionMatrix[i]).allMatch { e: Int -> e == 0 }
        }
        .mapToObj { i: Int -> tracks[i] }
        .collect(Collectors.toList())
}

private fun checkTracksCollision(trackA: IntArray, trackB: IntArray): Int {
    for (item in trackA)
        for (value in trackB)
            if (item == value)
                return 1

    return 0
}