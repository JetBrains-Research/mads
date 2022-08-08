package domain.objects

import domain.responses.DynamicResponse
import domain.responses.IDynamicResponse
import domain.responses.VDynamicResponse
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import kotlin.math.pow

class HHCellObject : PhysicalObject() {
    // constants
    // mS/cm^2
    private val g_L = 0.3
    private val g_K = 35.0
    private val g_Na = 120.0

    // mV
    private val E_L = -54.387
    private val E_K = -77.0
    private val E_Na = 50.0

    // mF/cm^2
    private val C_m = 1.0

    // dt
    private val dt = 0.02

    private val pulseVal = 100.0

    init {
        responseMapping[IDynamicResponse::class] = ::iDynamicResponse
    }

    private fun iDynamicResponse(response: Response): Array<ModelObject> {
        if (response is IDynamicResponse) {
            this.setSignal(response.signalId, 2.0)
        }

        return arrayOf(this)
    }

    private fun VDynamicResponse(response: Response): Array<ModelObject> {
        if (response is VDynamicResponse) {
            val I_e = this.getSignal(0)
            val V = this.getSignal(1)
            val n = this.getSignal(2)
            val m = this.getSignal(3)
            val h = this.getSignal(4)

            val IK = g_K * n.pow(4.0) * (V - E_K);
            val INa = g_Na * m.pow(3.0) * h * (V - E_Na);
            val IL = g_L * (V - E_L);

            val delta = ((I_e - IK - INa - IL) / C_m) * dt

            val prevValue = this.getSignal(response.signalId)
            this.setSignal(response.signalId, prevValue + delta)
        }

        return arrayOf(this)
    }

    private fun NDynamicResponse(response: Response): Array<ModelObject> {
        if (response is DynamicResponse) {
            val V = this.getSignal(1);
            val n = this.getSignal(2);

            val delta = ((AlphaN(V) * (1.0 - n)) - (BetaN(V) * n)) * dt

            val prevValue = this.getSignal(response.signalId)
            this.setSignal(response.signalId, prevValue + delta)
        }

        return arrayOf(this)
    }

    private fun MDynamicResponse(response: Response): Array<ModelObject> {
        if (response is DynamicResponse) {
            val V = this.getSignal(1)
            val m = this.getSignal(3)
            val delta = ((AlphaM(V) * (1.0 - m)) - (BetaM(V) * m)) * dt

            val prevValue = this.getSignal(response.signalId)
            this.setSignal(response.signalId, prevValue + delta)
        }

        return arrayOf(this)
    }

    private fun HDynamicResponse(response: Response): Array<ModelObject> {
        if (response is DynamicResponse) {
            val V = this.getSignal(1)
            val h = this.getSignal(4)

            val delta = ((AlphaH(V) * (1.0 - h)) - (BetaH(V) * h)) * dt

            val prevValue = this.getSignal(response.signalId)
            this.setSignal(response.signalId, prevValue + delta)
        }

        return arrayOf(this)
    }

    private fun AlphaN(V: Double): Double {
        return 0.01 * (V + 55.0) / (1.0 - Math.exp(-0.1 * (V + 55.0)))
    }

    private fun BetaN(V: Double): Double {
        return 0.125 * Math.exp(-0.0125 * (V + 65.0))
    }

    private fun AlphaM(V: Double): Double {
        return 0.1 * (V + 40.0) / (1.0 - Math.exp(-0.1 * (V + 40.0)))
    }

    private fun BetaM(V: Double): Double {
        return 4.0 * Math.exp(-0.0556 * (V + 65.0))
    }

    private fun AlphaH(V: Double): Double {
        return 0.07 * Math.exp(-0.05 * (V + 65.0))
    }

    private fun BetaH(V: Double): Double {
        return 1.0 / (1.0 + Math.exp(-0.1 * (V + 35.0)))
    }
}