package org.jetbrains.research.mads.core.lattice

import com.google.common.util.concurrent.AtomicDoubleArray
import gnu.trove.map.hash.TIntObjectHashMap
import gnu.trove.set.hash.TIntHashSet
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.VolumetricState
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

class Lattice(private val size: Int, signals: Set<Signals>, private val stepShiftRestriction: Int = 5) {
    private val sizeSquare = size * size
    private val sizeCube = size * size * size
    private val cube: Array<Cell> = Array(sizeCube) { Cell() }
    private val concentrations: Map<String, AtomicDoubleArray> = signals.flatMap { sig ->
        sig.getFullProperties()
            .filter { it.value is Double }
            .keys.map { it to AtomicDoubleArray(sizeCube + 1) }
    }.toMap()

    // Map for border cells restrictions
    private val borders: TIntObjectHashMap<TIntHashSet> = TIntObjectHashMap()

    // Flat vectors indices of neighborhood per each cell
    private val nbh = IntArray(26)

    // Flat vectors indices of full neighborhood including coordinate of interest per each cell
    private val fnbh = IntArray(27)

    private val emptyModelObjectList = listOf<ModelObject>()

    init {
        // Vectors of neighborhood per each cell
        val nbhD = arrayOf(
            intArrayOf(-1, -1, -1),
            intArrayOf(-1, -1, 0),
            intArrayOf(-1, -1, 1),
            intArrayOf(-1, 0, -1),
            intArrayOf(-1, 0, 0),
            intArrayOf(-1, 0, 1),
            intArrayOf(-1, 1, -1),
            intArrayOf(-1, 1, 0),
            intArrayOf(-1, 1, 1),
            intArrayOf(0, -1, -1),
            intArrayOf(0, -1, 0),
            intArrayOf(0, -1, 1),
            intArrayOf(0, 0, -1),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 1, -1),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 1),
            intArrayOf(1, -1, -1),
            intArrayOf(1, -1, 0),
            intArrayOf(1, -1, 1),
            intArrayOf(1, 0, -1),
            intArrayOf(1, 0, 0),
            intArrayOf(1, 0, 1),
            intArrayOf(1, 1, -1),
            intArrayOf(1, 1, 0),
            intArrayOf(1, 1, 1)
        )

        // Vectors of full neighborhood including coordinate of interest per each cell
        val fnbhD = arrayOf(
            intArrayOf(-1, -1, -1),
            intArrayOf(-1, -1, 0),
            intArrayOf(-1, -1, 1),
            intArrayOf(-1, 0, -1),
            intArrayOf(-1, 0, 0),
            intArrayOf(-1, 0, 1),
            intArrayOf(-1, 1, -1),
            intArrayOf(-1, 1, 0),
            intArrayOf(-1, 1, 1),
            intArrayOf(0, -1, -1),
            intArrayOf(0, -1, 0),
            intArrayOf(0, -1, 1),
            intArrayOf(0, 0, -1),
            intArrayOf(0, 0, 0),
            intArrayOf(0, 0, 1),
            intArrayOf(0, 1, -1),
            intArrayOf(0, 1, 0),
            intArrayOf(0, 1, 1),
            intArrayOf(1, -1, -1),
            intArrayOf(1, -1, 0),
            intArrayOf(1, -1, 1),
            intArrayOf(1, 0, -1),
            intArrayOf(1, 0, 0),
            intArrayOf(1, 0, 1),
            intArrayOf(1, 1, -1),
            intArrayOf(1, 1, 0),
            intArrayOf(1, 1, 1)
        )

        for (i in nbhD.indices) nbh[i] = getFlatCoordinate(nbhD[i][0], nbhD[i][1], nbhD[i][2])

        for (i in fnbhD.indices) fnbh[i] = getFlatCoordinate(fnbhD[i][0], fnbhD[i][1], fnbhD[i][2])

        // border for lower x
        val x0 = intArrayOf(nbh[0], nbh[1], nbh[2], nbh[3], nbh[4], nbh[5], nbh[6], nbh[7], nbh[8])
        // border for higher x
        val xS = intArrayOf(nbh[17], nbh[18], nbh[19], nbh[20], nbh[21], nbh[22], nbh[23], nbh[24], nbh[25])

        // border for lower y
        val y0 = intArrayOf(nbh[0], nbh[1], nbh[2], nbh[9], nbh[10], nbh[11], nbh[17], nbh[18], nbh[19])
        // border for higher y
        val yS = intArrayOf(nbh[6], nbh[7], nbh[8], nbh[14], nbh[15], nbh[16], nbh[23], nbh[24], nbh[25])

        // border for lower z
        val z0 = intArrayOf(nbh[0], nbh[3], nbh[6], nbh[9], nbh[12], nbh[14], nbh[17], nbh[20], nbh[23])
        // border for higher z
        val zS = intArrayOf(nbh[2], nbh[5], nbh[8], nbh[11], nbh[13], nbh[16], nbh[19], nbh[22], nbh[25])

        for (x in 0 until size) for (y in 0 until size) for (z in 0 until size) {
            val coordinate: Int = getFlatCoordinate(x, y, z)
            if (x == 0 || x == size - 1 || y == 0 || y == size - 1 || z == 0 || z == size - 1) {
                val restricted = TIntHashSet()
                borders.putIfAbsent(coordinate, restricted)
                if (x == 0) restricted.addAll(x0)
                if (x == size - 1) restricted.addAll(xS)
                if (y == 0) restricted.addAll(y0)
                if (y == size - 1) restricted.addAll(yS)
                if (z == 0) restricted.addAll(z0)
                if (z == size - 1) restricted.addAll(zS)
            }
        }
    }

    fun getObjects(coordinate: Int): List<ModelObject> {
        return if (checkIndex(coordinate))
            cube[coordinate].getObjects()
        else
            emptyModelObjectList
    }

    fun addObject(obj: ModelObject): ModelObject? {
        return if (checkIndex(obj.coordinate)) {
            val cell: Cell = cube[obj.coordinate]
            cell.putObject(obj)
            return obj
        } else {
            null
        }
    }

    fun removeObject(obj: ModelObject): ModelObject? {
        return if (checkIndex(obj.coordinate)) {
            val cell: Cell = cube[obj.coordinate]
            cell.removeObject(obj)
            return obj
        } else {
            null
        }
    }

    fun applyTrack(track: Track): List<ModelObject> {
        val result: ArrayList<ModelObject> = ArrayList()
        for (i in 0 until track.coordinates.size - 1) {
            for (obj in track.objects[i]) {
                moveObject(obj, track.coordinates[i + 1])
                obj.coordinate = track.coordinates[i + 1]
                result.add(obj)
            }
        }
        return result
    }

    private fun moveObject(obj: ModelObject, dstCoordinate: Int): ModelObject? {
        return if (checkIndex(obj.coordinate) && checkIndex(dstCoordinate)) {
            val srcCell: Cell = cube[obj.coordinate]
            val dstCell: Cell = cube[dstCoordinate]
            srcCell.removeObject(obj)
            dstCell.putObject(obj)
            obj.coordinate = dstCoordinate
            return obj
        } else {
            null
        }
    }

    private fun checkIndex(coordinate: Int): Boolean {
        return coordinate >= 0 && coordinate < cube.size
    }

    // region Functions for object moving

    // Retrieving candidates for different chemical and spatial state
    // Retrieving tracks for two types of movement: switch and shift
    // Applying track on the lattice

    fun getRandomCandidate(entryCoordinate: Int, state: VolumetricState, roll: (Int) -> Int): Int {
        val restricted = borders[entryCoordinate]
        val candidateIndices = IntArray(28)
        for (j in fnbh) {
            val checkCoordinate = entryCoordinate + j
            if (isOutsideBorder(restricted, j) || checkVolumetricState(checkCoordinate, state)) continue
            candidateIndices[++candidateIndices[0]] = j
        }
        return rollCandidate(candidateIndices, roll)
    }

    fun getGradientCandidate(entryCoordinate: Int, state: VolumetricState, signal: String, roll: (Int) -> Int): Int {
        val restricted = borders[entryCoordinate]
        val candidateIndices = IntArray(28)
        var maxDiff = Double.MIN_VALUE
        for (j in fnbh) {
            val checkCoordinate = entryCoordinate + j
            if (isOutsideBorder(restricted, j) || checkVolumetricState(checkCoordinate, state)) continue
            val diff = concentrations[signal]!![checkCoordinate] - concentrations[signal]!![entryCoordinate]
            if (maxDiff <= diff) {
                maxDiff = diff
                candidateIndices[++candidateIndices[0]] = j
            }
        }
        return rollCandidate(candidateIndices, roll)
    }

    fun getAntigradientCandidate(entryCoordinate: Int, state: VolumetricState, signal: String, roll: (Int) -> Int): Int {
        val restricted = borders[entryCoordinate]
        val candidateIndices = IntArray(28)
        var minDiff = Double.MAX_VALUE
        for (j in fnbh) {
            val checkCoordinate = entryCoordinate + j
            if (isOutsideBorder(restricted, j) || checkVolumetricState(checkCoordinate, state)) continue
            val diff = concentrations[signal]!![checkCoordinate] - concentrations[signal]!![entryCoordinate]
            if (minDiff >= diff) {
                minDiff = diff
                candidateIndices[++candidateIndices[0]] = j
            }
        }
        return rollCandidate(candidateIndices, roll)
    }

    fun getInsolubleCandidate(entryCoordinate: Int, state: VolumetricState, type: String, roll: (Int) -> Int): Int {
        val neighbours: List<List<ModelObject>> = getNeighbours(entryCoordinate, state)
        val candidateIndices = IntArray(28)
        for (i in neighbours.indices) {
            for (j in neighbours[i].indices) {
                if (neighbours[i][j].type == type) {
                    candidateIndices[++candidateIndices[0]] = fnbh[i]
                    break
                }
            }
        }
        return rollCandidate(candidateIndices, roll)
    }

    fun getShiftTrackCandidate(obj: ModelObject, vector: Int, roll: () -> Double): Track {
        if (vector == 0)
            return EmptyTrack

        val track = ShiftTrack(roll())
        var currentVolume = obj.volume
        var currentTrackPosition = obj.coordinate

        for (i in 0..stepShiftRestriction) {
            val restricted = borders[currentTrackPosition]
            if (restricted != null && restricted.contains(vector) || i == stepShiftRestriction)
                return EmptyTrack

            currentTrackPosition += vector
            val cell: Cell = cube[currentTrackPosition]
            val objToMove: List<ModelObject> = cell.getMovingCandidates(currentVolume)
            track.add(currentTrackPosition, objToMove)

            if (objToMove.isEmpty())
                break

            currentVolume = cell.getSumVolume(objToMove)
        }

        return track
    }

    fun getSwitchTrackCandidate(obj: ModelObject, vector: Int, roll: () -> Double): Track {
        val restricted = borders[obj.coordinate]
        if (vector == 0 || (restricted != null && restricted.contains(vector)))
            return EmptyTrack

        val track = SwitchTrack(roll())
        val switchCoordinate = obj.coordinate + vector

        val switchCell: Cell = cube[switchCoordinate]
        val objToMove: List<ModelObject> = switchCell.getMovingCandidates(obj.volume)

        track.add(obj.coordinate, listOf(obj))
        track.add(switchCoordinate, objToMove)
        track.add(obj.coordinate, listOf())

        return track
    }

    fun getDirectTrackCandidate(obj: ModelObject, vector: Int, roll: () -> Double): Track {
        val restricted = borders[obj.coordinate]
        if (vector == 0 || (restricted != null && restricted.contains(vector)))
            return EmptyTrack

        val track = DirectTrack(roll())
        val directCoordinate = obj.coordinate + vector

        track.add(obj.coordinate, listOf(obj))
        track.add(directCoordinate, listOf())

        return track
    }

    // TODO: we can check here if potential cell actually have enough of free space between 0.0 and 1.0
    private fun checkVolumetricState(coordinate: Int, state: VolumetricState): Boolean {
        return state == VolumetricState.Free && cube[coordinate].occupiedVolume.get() > 0.0 || state == VolumetricState.Any
    }

    private fun getNeighbours(entryCoordinate: Int, state: VolumetricState): List<List<ModelObject>> {
        val restricted = borders[entryCoordinate]
        val neighbours = List<ArrayList<ModelObject>>(27) { ArrayList() }
        for (i in fnbh.indices) {
            val checkCoordinate = entryCoordinate + fnbh[i]
            if (!(isOutsideBorder(restricted, fnbh[i]) || checkVolumetricState(checkCoordinate, state))) {
                neighbours[i].addAll(cube[checkCoordinate].getObjects())
            }
        }
        return neighbours
    }

    private fun isOutsideBorder(restricted: TIntHashSet?, vector: Int): Boolean {
        return restricted != null && restricted.contains(vector)
    }

    private fun rollCandidate(candidateIndices: IntArray, roll: (Int) -> Int): Int {
        return if (candidateIndices[0] != 0) candidateIndices[roll(candidateIndices[0]) + 1] else 0
    }

    // endregion

    // region Diffusion and concentrations

    fun getLeaked(signal: String): Double {
        return concentrations[signal]!![sizeCube]
    }

    fun getConcentration(signal: String, coordinate: Int): Double {
        return concentrations[signal]!![coordinate]
    }

    fun updateConcentration(signal: String, coordinate: Int, value: Double) {
        // here we update concentrations after spread/gather of signal
        concentrations[signal]!!.addAndGet(coordinate, value)
    }

    fun updateDiffusion(signal: String, diff: AtomicDoubleArray): List<ModelObject> {
        concentrations[signal]?.addAndGet(sizeCube, diff[sizeCube])
        return IntStream.range(0, sizeCube)
            .parallel()
            .filter { i: Int -> diff[i] != 0.0 }
            .peek { i: Int -> concentrations[signal]!!.addAndGet(i, diff[i]) }
            .mapToObj { i: Int -> cube[i].getObjects().stream() }
            .flatMap { i: Stream<ModelObject> -> i.map { obj: ModelObject -> obj } }
            .collect(Collectors.toList())
    }

    fun calcDiffusion(signal: String, diffusionRate: Double): AtomicDoubleArray {
        val diffResult = AtomicDoubleArray(sizeCube + 1)
        IntStream.range(0, sizeCube)
            .parallel()
            .forEach { i: Int ->
                val diffusedAmount = concentrations[signal]!![i] * diffusionRate // total diffused amount
                if (diffusedAmount == 0.0) return@forEach   // if nothing to diffuse - skip
                val dr = diffusedAmount / 26 // diffusion per cell pair
                diffResult.addAndGet(i, -diffusedAmount) // update current cell
                val restricted = borders[i]
                for (dims in nbh) {
                    if (restricted != null && restricted.contains(dims)) {
                        diffResult.addAndGet(sizeCube, dr) // tracking diffusion outside borders
                        continue
                    }
                    val di = i + dims // flat cells
                    diffResult.addAndGet(di, dr) // update neighbors cells
                }
            }
        return diffResult
    }

    // endregion

    private fun getFlatCoordinate(x: Int, y: Int, z: Int): Int {
        return size * (x * size + y) + z
    }

    private fun restoreCoordinateFromFlat(flatCoordinate: Int): IntArray {
        val restored = IntArray(3)
        restored[2] = flatCoordinate % size
        restored[1] = flatCoordinate / size % size
        restored[0] = flatCoordinate / sizeSquare
        return restored
    }
}

val emptyLattice = Lattice(0, setOf())