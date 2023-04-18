package org.jetbrains.research.mads.ns.electrode

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads.ns.physiology.neurons.ProbabilisticSpikingSignals
import java.util.*

object ElectrodeConnection : ConnectionType

class PeriodicPulsationSignals(cycleCounter: Int = 100, pulseValue: Double = 5.0) : Signals() {
    var cycle: Int by observable(cycleCounter)
    var iteration: Int by observable(0)
    var pulse: Double by observable(pulseValue)
}

class NoiseSignals(meanValue: Double = 0.0, std: Double = 1.0) : Signals() {
    val meanValue: Double by observable(meanValue)
    val std: Double by observable(std)
}

class Electrode(val rnd: Random, vararg signals: Signals) : ModelObject(ProbabilisticSpikingSignals(), *signals)

class PulseConstants(val pulseValue: Double = 5.0) : MechanismConstants

//class NoiseConstants(val std: Double = 2.5, val meanValue: Double = 5.0) : MechanismConstants

object ElectrodeMechanisms {
    val PeriodicPulseDynamic = Electrode::PeriodicPulseDynamic
    val PulseDynamic = Electrode::PulseDynamic
    val NoiseDynamic = Electrode::NoiseDynamic
}

fun Electrode.PeriodicPulseDynamic(params: MechanismParameters): List<Response> {
    val s = this.signals[CurrentSignals::class] as CurrentSignals
    val pps = this.signals[PeriodicPulsationSignals::class] as PeriodicPulsationSignals
    val iteration = pps.iteration
    val cycle = pps.cycle

    if (iteration % cycle == 0) {
        return arrayListOf(
            this.createResponse {
                s.I_e += pps.pulse
                pps.iteration++
            }
        )
    } else if (s.I_e > 0.0) {
        return arrayListOf(
            this.createResponse {
                s.I_e -= pps.pulse
                pps.iteration++
            }
        )
    } else {
        return arrayListOf(
            this.createResponse {
                pps.iteration++
            }
        )
    }
}

fun Electrode.PulseDynamic(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val spikeProbability = (this.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals).spikeProbability

    var I = 0.0
    if (currentSignals.I_e == 0.0 && rnd.nextDouble() < spikeProbability) {
        I = (params.constants as PulseConstants).pulseValue
    }
    val delta = I - currentSignals.I_e

    result.add(
        this.createResponse {
            currentSignals.I_e += delta
        }
    )

    this.connections[ElectrodeConnection]?.forEach {
        val receiverCurrentSignals = it.signals[CurrentSignals::class] as CurrentSignals
        result.add(
            it.createResponse {
                receiverCurrentSignals.I_e += delta
            }
        )
    }

    return result
}

fun Electrode.NoiseDynamic(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val noiseSignals = this.signals[NoiseSignals::class] as NoiseSignals

    val newI = rnd.nextGaussian() * noiseSignals.std + noiseSignals.meanValue
    val delta = newI - currentSignals.I_e

    result.add(
        this.createResponse {
            currentSignals.I_e += delta
        }
    )

    this.connections[ElectrodeConnection]?.forEach {
        val receiverCurrentSignals = it.signals[CurrentSignals::class] as CurrentSignals
        result.add(
            it.createResponse {
                receiverCurrentSignals.I_e += delta
            }
        )
    }

    return result
}