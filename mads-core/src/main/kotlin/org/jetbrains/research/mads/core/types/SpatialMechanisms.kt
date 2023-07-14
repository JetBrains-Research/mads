package org.jetbrains.research.mads.core.types

import org.jetbrains.research.mads.core.lattice.emptyLattice
import kotlin.random.Random
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

open class SpatialConstants(val signal: KProperty1<out Signals, Double>): MechanismConstants {
    val signalString: String = Signals.getName(signal)
}

class MoveConstants(
    signal: KProperty1<out Signals, Double>, //TODO: this one won't work in that way for insoluble types, but we can??? switch implementation of type as a Signals
    val movementType: MovementType,
    val directionSelection: DirectionSelection,
    val precision: Int = 12
) : SpatialConstants(signal)

class DiffuseConstants(
    signal: KProperty1<out Signals, Double>,
    val rate: Double,
    val precision: Int = 12
) : SpatialConstants(signal)

class TransferSignalConstants(
    signal: KProperty1<out Signals, Double>,
    val fractionFn: (Double) -> Double
) : SpatialConstants(signal)

class MovingSignals(val random: Random) : Signals()

fun ModelObject.getParentalSignalValue(spatialConstants: SpatialConstants): Double {
    return if (this.parent.lattice != emptyLattice) {
        this.parent.lattice.getConcentration(spatialConstants.signalString, this.coordinate)
    } else {
        @Suppress("UNCHECKED_CAST")
        val prop = spatialConstants.signal as KProperty1<Signals, Double>
        val signalType = prop.javaField?.declaringClass?.kotlin
        val parentSignal = this.parent.signals[signalType]!!
        spatialConstants.signal.get(parentSignal)
    }
}

fun ModelObject.updateParentalSignalValue(spatialConstants: SpatialConstants, delta: Double) {
    @Suppress("UNCHECKED_CAST")
    val prop = spatialConstants.signal as KMutableProperty1<Signals, Double>
    val signalType = prop.javaField?.declaringClass?.kotlin
    val parentSignal = this.parent.signals[signalType]!!
    val currentValue = prop.get(parentSignal)
    prop.set(parentSignal, currentValue + delta)

    if (this.parent.lattice != emptyLattice) {
        val latticeValue =
            this.parent.lattice.getConcentration(spatialConstants.signalString, this.coordinate)
        this.parent.lattice.updateConcentration(
            spatialConstants.signalString,
            this.coordinate,
            latticeValue + delta
        )
    }
}

@ExperimentalMechanism
@ConstantType(type = MoveConstants::class)
fun ModelObject.move(parameters: MechanismParameters) : List<Response> {
    val moveConstants = parameters.constants as MoveConstants
    val selector = (this.signals[MovingSignals::class] as MovingSignals).random
    val candidate: Int = when(moveConstants.directionSelection) {
        DirectionSelection.Random ->
            this.parent.lattice.getRandomCandidate(coordinate, volume, Random::nextInt)
        DirectionSelection.Gradient ->
            this.parent.lattice.getGradientCandidate(coordinate, volume, moveConstants.signalString, moveConstants.precision, selector::nextInt)
        DirectionSelection.Antigradient ->
            this.parent.lattice.getAntigradientCandidate(coordinate, volume, moveConstants.signalString, moveConstants.precision, selector::nextInt)
        DirectionSelection.Insoluble ->
            this.parent.lattice.getInsolubleCandidate(coordinate, volume, moveConstants.signalString, selector::nextInt)
    }
    val track = when(moveConstants.movementType) {
        MovementType.Direct -> this.parent.lattice.getDirectTrackCandidate(this, candidate, selector::nextDouble)
        MovementType.Switch -> this.parent.lattice.getSwitchTrackCandidate(this, candidate, selector::nextDouble)
    }

    return listOf(
        this.parent.createResponse(conflict = track.createSpatialConflict()) {
            this.parent.lattice.applyTrack(track)
        }
    )
}

@ExperimentalMechanism
@ConstantType(type = DiffuseConstants::class)
fun ModelObject.diffuse(parameters: MechanismParameters) : List<Response> {
    val diffuseConstants = parameters.constants as DiffuseConstants
    val diff = lattice.calcDiffusion(diffuseConstants.signalString, diffuseConstants.rate, diffuseConstants.precision)

    return listOf(
        this.createResponse {
            lattice.updateDiffusion(diffuseConstants.signalString, diff)
        }
    )
}

@ExperimentalMechanism
@ConstantType(type = TransferSignalConstants::class)
fun ModelObject.signalIn(parameters: MechanismParameters) : List<Response> {
    val transferSignalConstants = parameters.constants as TransferSignalConstants
    return if (Signals.isValidSignalProperty(transferSignalConstants.signal, Double::class)) {

        @Suppress("UNCHECKED_CAST")
        val prop = transferSignalConstants.signal as KMutableProperty1<Signals, Double>
        val signalType = prop.javaField?.declaringClass?.kotlin
        val currentSignal = this.signals[signalType]!!
        val amount = transferSignalConstants.fractionFn(getParentalSignalValue(transferSignalConstants))

        listOf(
            this.createResponse {
                this.updateParentalSignalValue(transferSignalConstants, -amount)
            },
            this.createResponse {
                val currentValue = prop.get(currentSignal)
                prop.set(currentSignal, currentValue + amount)
            },
        )
    } else
        EmptyResponseList
}

@ExperimentalMechanism
@ConstantType(type = TransferSignalConstants::class)
fun ModelObject.signalOut(parameters: MechanismParameters) : List<Response> {
    val transferSignalConstants = parameters.constants as TransferSignalConstants
    return if (Signals.isValidSignalProperty(transferSignalConstants.signal, Double::class)) {

        @Suppress("UNCHECKED_CAST")
        val prop = transferSignalConstants.signal as KMutableProperty1<Signals, Double>
        val signalType = prop.javaField?.declaringClass?.kotlin
        val currentSignal = this.signals[signalType]!!
        val amount = transferSignalConstants.fractionFn(prop.get(currentSignal))

        listOf(
            this.parent.createResponse {
                this.updateParentalSignalValue(transferSignalConstants, amount)
            },
            this.createResponse {
                val currentValue = prop.get(currentSignal)
                prop.set(currentSignal, currentValue - amount)
            }
        )
    } else
        EmptyResponseList
}