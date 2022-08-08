package domain.mechanisms

import domain.Signals
import domain.SimpleParameters
import domain.objects.HHCellObject
import domain.objects.HHConstants
import domain.objects.HHSignals
import domain.responses.DynamicResponse
import domain.responses.IDynamicResponse
import org.jetbrains.research.mads.core.types.Response
import kotlin.math.exp
import kotlin.math.pow

fun HHCellObject.IDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    this.signals as HHSignals
    val delta: Double = 0.0

    val responseString = String.format("Object: %s, Signal: I", this.type)
    return arrayOf(DynamicResponse(responseString, this, delta) { this.signals.I += it })

}

fun HHCellObject.VDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    this.signals as HHSignals

    val I_e = this.signals.I
    val V = this.signals.V
    val n = this.signals.N
    val m = this.signals.M
    val h = this.signals.H

    val IK = HHConstants.g_K * n.pow(4.0) * (V - HHConstants.E_K);
    val INa = HHConstants.g_Na * m.pow(3.0) * h * (V - HHConstants.E_Na);
    val IL = HHConstants.g_L * (V - HHConstants.E_L);

    val delta = ((I_e - IK - INa - IL) / HHConstants.C_m) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: V", this.type)
    return arrayOf(DynamicResponse(responseString, this, delta) { this.signals.V += it })

}

fun HHCellObject.NDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    this.signals as HHSignals

    val V = this.signals.V
    val n = this.signals.N

    val delta = ((AlphaN(V) * (1.0 - n)) - (BetaN(V) * n)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: N", this.type)
    return arrayOf(DynamicResponse(responseString, this, delta) { this.signals.N += it })
}

fun HHCellObject.MDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    this.signals as HHSignals

    val V = this.signals.V
    val m = this.signals.M

    val delta = ((AlphaM(V) * (1.0 - m)) - (BetaM(V) * m)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: M", this.type)
    return arrayOf(DynamicResponse(responseString, this, delta) { this.signals.M += it })

}

fun HHCellObject.HDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    this.signals as HHSignals

    val V = this.signals.V
    val h = this.signals.H

    val delta = ((AlphaH(V) * (1.0 - h)) - (BetaH(V) * h)) * HHConstants.dt

    val responseString = String.format("Object: %s, Signal: H", this.type)
    return arrayOf(DynamicResponse(responseString, this, delta) { this.signals.M += it })
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