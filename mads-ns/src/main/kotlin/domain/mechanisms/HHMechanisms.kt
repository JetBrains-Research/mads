package domain.mechanisms

import domain.HHConstants
import domain.HHParameters
import domain.objects.HHCellObject
import domain.objects.HHSignals
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import kotlin.math.exp
import kotlin.math.pow

fun HHCellObject.IDynamicMechanism(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals
    val delta: Double = 0.0
//    val delta = (Random.nextDouble() - 0.5)/10

    val responseString = String.format("Object: %s, Signal: I", this.type)
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.I += it })
    return arrayListOf(
        DynamicResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateI
        )
    )
}

fun HHCellObject.VDynamicMechanism(params: HHParameters): List<Response> {
    val s = this.signals[HHSignals::class] as HHSignals
    val c = params.constants as HHConstants

    val IK = c.g_K * s.N.pow(4.0) * (s.V - c.E_K);
    val INa = c.g_Na * s.M.pow(3.0) * s.H * (s.V - c.E_Na);
    val IL = c.g_L * (s.V - c.E_L);

    val delta = ((s.I_e - IK - INa - IL) / c.C_m) * c.dt

    val responseString = "${this.hashCode()}, dV, ${delta}\n"
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.V += it })
    return arrayListOf(
        DynamicResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateV
        )
    )
}

fun HHCellObject.NDynamicMechanism(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val n = signals.N

    val delta = ((AlphaN(V) * (1.0 - n)) - (BetaN(V) * n)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: N", this.type)
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.N += it })
    return arrayListOf(
        DynamicResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateN
        )
    )
}

fun HHCellObject.MDynamicMechanism(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val m = signals.M

    val delta = ((AlphaM(V) * (1.0 - m)) - (BetaM(V) * m)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: M", this.type)
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.M += it })
    return arrayListOf(
        DynamicResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateM
        )
    )
}

fun HHCellObject.HDynamicMechanism(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val h = signals.H

    val delta = ((AlphaH(V) * (1.0 - h)) - (BetaH(V) * h)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: H", this.type)
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.H += it })
    return arrayListOf(
        DynamicResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateH
        )
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