package domain.mechanisms

import domain.SimpleParameters
import domain.objects.HHCellObject
import domain.objects.HHConstants
import domain.objects.HHSignals
import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import org.jetbrains.research.mads.core.types.Response
import kotlin.math.exp
import kotlin.math.pow

fun HHCellObject.IDynamicMechanism(params: SimpleParameters) : List<Response>
{
    val signals = this.signals[HHSignals::class] as HHSignals
    val delta: Double = 0.0
//    val delta = (Random.nextDouble() - 0.5)/10

    val responseString = String.format("Object: %s, Signal: I", this.type)
    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.I += it })
//    return arrayListOf(DynamicResponse(responseString, this, delta, this::updateI))

}

fun HHCellObject.VDynamicMechanism(params: SimpleParameters) : List<Response>
{
    val signals = this.signals[HHSignals::class] as HHSignals

    val I_e = signals.I
    val V = signals.V
    val n = signals.N
    val m = signals.M
    val h = signals.H

    val IK = HHConstants.g_K * n.pow(4.0) * (V - HHConstants.E_K);
    val INa = HHConstants.g_Na * m.pow(3.0) * h * (V - HHConstants.E_Na);
    val IL = HHConstants.g_L * (V - HHConstants.E_L);

    val delta = ((I_e - IK - INa - IL) / HHConstants.C_m) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: V", this.type)
    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.V += it })
//    return arrayListOf(DynamicResponse(responseString, this, delta, this::updateV))
}

fun HHCellObject.NDynamicMechanism(params: SimpleParameters) : List<Response>
{
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val n = signals.N

    val delta = ((AlphaN(V) * (1.0 - n)) - (BetaN(V) * n)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: N", this.type)
    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.N += it })
//    return arrayListOf(DynamicResponse(responseString, this, delta, this::updateN))
}

fun HHCellObject.MDynamicMechanism(params: SimpleParameters) : List<Response>
{
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val m = signals.M

    val delta = ((AlphaM(V) * (1.0 - m)) - (BetaM(V) * m)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: M", this.type)
    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.M += it })
//    return arrayListOf(DynamicResponse(responseString, this, delta, this::updateM))
}

fun HHCellObject.HDynamicMechanism(params: SimpleParameters) : List<Response>
{
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val h = signals.H

    val delta = ((AlphaH(V) * (1.0 - h)) - (BetaH(V) * h)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: H", this.type)
    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.H += it })
//    return arrayListOf(DynamicResponse(responseString, this, delta, this::updateH))
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