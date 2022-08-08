package domain.objects

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class HHCellObject(override val signals: Signals) : SignalsObject(signals) {
    fun updateI(delta: Double)
    {
        this.signals as HHSignals
        this.signals.I += delta
    }

    fun updateV(delta: Double)
    {
        this.signals as HHSignals
        this.signals.V += delta
    }

    fun updateN(delta: Double)
    {
        this.signals as HHSignals
        this.signals.N += delta
    }

    fun updateM(delta: Double)
    {
        this.signals as HHSignals
        this.signals.M += delta
    }

    fun updateH(delta: Double)
    {
        this.signals as HHSignals
        this.signals.H += delta
    }
}

data class HHSignals(var I: Double = 8.0, var V: Double = -65.0, var N: Double = 0.32, var M: Double = 0.05, var H: Double = 0.6) :
    Signals

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