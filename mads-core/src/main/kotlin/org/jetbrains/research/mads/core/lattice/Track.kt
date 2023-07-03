package org.jetbrains.research.mads.core.lattice

import org.jetbrains.research.mads.core.types.Conflict
import org.jetbrains.research.mads.core.types.ConflictSubject
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.stream.Collectors
import java.util.stream.IntStream

sealed class Track(val rolledRnd: Double) : ConflictSubject {
    val coordinates: ArrayList<Int> = ArrayList()
    val objects: ArrayList<List<ModelObject>> = ArrayList()
    var objectsCount: Int = 0
        private set

    companion object {
        private fun resolveSpatialConflicts(responses: List<Response>): List<Response> {
            val tracks = responses.map { it.conflict.conflictSubject as Track }
            val resolvedTracks = Track.resolveCollisions(tracks).toSet()
            return responses.filter { resolvedTracks.contains(it.conflict.conflictSubject) }
        }

        private fun resolveCollisions(tracks: List<Track>): List<Track> {
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
    }

    fun add(coordinate: Int, objs: List<ModelObject>) {
        coordinates.add(coordinate)
        objects.add(objs)
        objectsCount += objs.size
    }

    fun createSpatialConflict(): Conflict {
        return Conflict(this, ::resolveSpatialConflicts)
    }
}

class ShiftTrack(rolledRnd: Double) : Track(rolledRnd)

class SwitchTrack(rolledRnd: Double) : Track(rolledRnd)

object EmptyTrack : Track(0.0)
