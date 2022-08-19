package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.ConnectionType
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads_ns.hh.HHCell
import kotlin.random.Random

object ElectrodeConnection : ConnectionType

class Electrode(current: CurrentSignals, val rnd: Random) : SignalsObject(current) {
    fun updateI(delta: Double) {
        val sig = this.signals[CurrentSignals::class] as CurrentSignals
        sig.I_e = delta
    }

    fun connectToHHCell(cell: HHCell) {
        this.connections[ElectrodeConnection] = hashSetOf(cell)
        cell.connections[ElectrodeConnection] = hashSetOf(this)
    }
}