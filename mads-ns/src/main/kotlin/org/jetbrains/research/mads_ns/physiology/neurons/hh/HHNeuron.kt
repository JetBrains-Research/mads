package org.jetbrains.research.mads_ns.physiology.neurons.hh

import org.jetbrains.research.mads.core.types.Constants
import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import org.jetbrains.research.mads_ns.physiology.neurons.PotentialSignals
import kotlin.math.exp
import kotlin.math.pow

object HHConstants : Constants {
    // constants
    // mS/cm^2
    const val g_L = 0.3
    const val g_K = 35.0
    const val g_Na = 120.0

    // mV
    const val E_L = -54.387
    const val E_K = -77.0
    const val E_Na = 50.0

    // mF/cm^2
    const val C_m = 1.0

    // dt
    const val dt = 0.02
    const val V_thresh = -50.0

    const val pulseVal = 100.0
}

data class HHSignals(
    var N: Double = 0.32,
    var M: Double = 0.05,
    var H: Double = 0.6
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

object HHMechanisms {
    val VDynamic = HHNeuron::VDynamic
    val HDynamic = HHNeuron::HDynamic
    val NDynamic = HHNeuron::NDynamic
    val MDynamic = HHNeuron::MDynamic
}

class HHNeuron(spikeThreshold: Double, vararg signals: Signals) : Neuron(spikeThreshold, *signals)

fun HHNeuron.VDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val s = this.signals[HHSignals::class] as HHSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals

    val IK = HHConstants.g_K * s.N.pow(4.0) * (u.V - HHConstants.E_K)
    val INa = HHConstants.g_Na * s.M.pow(3.0) * s.H * (u.V - HHConstants.E_Na)
    val IL = HHConstants.g_L * (u.V - HHConstants.E_L)

    val delta = ((i.I_e - IK - INa - IL) / HHConstants.C_m) * HHConstants.dt

    return arrayListOf(
        this.createResponse("dV,${delta},\n") {
            u.V += delta
        }
    )
}

fun HHNeuron.NDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val s = this.signals[HHSignals::class] as HHSignals

    val V = u.V
    val n = s.N

    val delta = ((AlphaN(V) * (1.0 - n)) - (BetaN(V) * n)) * HHConstants.dt

    return arrayListOf(
        this.createResponse("dN,${delta},\n") {
            s.N += delta
        }
    )
}

fun HHNeuron.MDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val s = this.signals[HHSignals::class] as HHSignals

    val V = u.V
    val m = s.M

    val delta = ((AlphaM(V) * (1.0 - m)) - (BetaM(V) * m)) * HHConstants.dt

    return arrayListOf(
        this.createResponse("dM,${delta},\n") {
            s.M += delta
        }
    )
}

fun HHNeuron.HDynamic(params: MechanismParameters): List<Response> {
    val u = this.signals[PotentialSignals::class] as PotentialSignals
    val s = this.signals[HHSignals::class] as HHSignals

    val V = u.V
    val h = s.H

    val delta = ((AlphaH(V) * (1.0 - h)) - (BetaH(V) * h)) * HHConstants.dt

    return arrayListOf(
        this.createResponse("dH,${delta},\n") {
            s.H += delta
        }
    )
}

private fun AlphaN(V: Double): Double {
    return 0.01 * (V + 55.0) / (1.0 - exp(-0.1 * (V + 55.0)))
}

private fun BetaN(V: Double): Double {
    return 0.125 * exp(-0.0125 * (V + 65.0))
}

private fun AlphaM(V: Double): Double {
    return 0.1 * (V + 40.0) / (1.0 - exp(-0.1 * (V + 40.0)))
}

private fun BetaM(V: Double): Double {
    return 4.0 * exp(-0.0556 * (V + 65.0))
}

private fun AlphaH(V: Double): Double {
    return 0.07 * exp(-0.05 * (V + 65.0))
}

private fun BetaH(V: Double): Double {
    return 1.0 / (1.0 + exp(-0.1 * (V + 35.0)))
}