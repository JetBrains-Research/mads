package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.ProbabilisticSpikingSignals
import kotlin.random.Random

object ElectrodeConnection : ConnectionType

class Electrode(current: CurrentSignals, val rnd: Random) : ModelObject(current, ProbabilisticSpikingSignals(0.0))

object ElectrodeConstants : Constants {
    // constants
    const val pulseProbability: Double = 0.5
    const val pulseValue: Double = 5.0
}

object ElectrodeMechanisms {
    val PulseDynamic = Electrode::PulseDynamic
}

fun Electrode.PulseDynamic(params: MechanismParameters): List<Response> {
    val s = this.signals[CurrentSignals::class] as CurrentSignals
    val spikeProbability = (this.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals).spikeProbability

    var I = 0.0
    if (s.I_e == 0.0 && rnd.nextDouble() < spikeProbability) {
        I = (params.constants as ElectrodeConstants).pulseValue
    }

    val delta = I - s.I_e

    return arrayListOf(
        this.createResponse {
            s.I_e += delta
        }
    )
}