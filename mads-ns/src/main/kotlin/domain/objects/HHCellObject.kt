package domain.objects

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class HHCellObject(vararg sig: Signals, val constantCurrent: Boolean = true) : SignalsObject(*sig) {
    fun updateI(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.I_e += delta
    }

    fun updateV(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.V += delta
    }

    fun updateN(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.N += delta
    }

    fun updateM(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.M += delta
    }

    fun updateH(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.H += delta
    }

    fun updateIexternal(delta: Double) {
        val sig = this.signals[HHSignals::class] as HHSignals
        sig.I_ext += delta
    }
}

data class HHSignals(
    var I_e: Double = 8.0,
    var V: Double = -65.0,
    var N: Double = 0.32,
    var M: Double = 0.05,
    var H: Double = 0.6,
    var I_ext: Double = 0.0
) : Signals {
    override fun clone() : Signals
    {
        return  this.copy()
    }
}