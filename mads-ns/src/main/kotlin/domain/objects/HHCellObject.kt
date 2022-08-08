package domain.objects

import domain.Signals
import domain.responses.DynamicResponse
import domain.responses.IDynamicResponse
import domain.responses.VDynamicResponse
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import kotlin.math.pow

class HHCellObject(override val signals: Signals) : PhysicalObject(signals) {

}

data class HHSignals(var I: Double = 65.0, var V: Double = 0.0, var N: Double = 0.0, var M: Double = 0.0, var H: Double = 0.0) : Signals

object HHConstants{
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

    const val pulseVal = 100.0
}