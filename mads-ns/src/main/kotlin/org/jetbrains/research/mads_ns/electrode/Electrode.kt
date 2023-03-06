package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import kotlin.random.Random

object ElectrodeConnection : ConnectionType

class Electrode(current: CurrentSignals, val rnd: Random) : ModelObject(current)

object ElectrodeConstants : Constants {
    // constants
    const val pulseProbability: Double = 0.5
    const val pulseValue: Double = 10.0
}

object ElectrodeMechanisms {
    val PulseDynamic = Electrode::PulseDynamic
}

fun Electrode.PulseDynamic(params: MechanismParameters): List<Response> {
    val s = this.signals[CurrentSignals::class] as CurrentSignals
    val consts = params.constants as ElectrodeConstants

    var I = 0.0
    if (s.I_e == 0.0 && rnd.nextDouble() < consts.pulseProbability) {
        I = consts.pulseValue
    }

    val delta = I - s.I_e

    return arrayListOf(
        this.createResponse("dI",delta.toString()) {
            s.I_e += delta
        }
    )
}