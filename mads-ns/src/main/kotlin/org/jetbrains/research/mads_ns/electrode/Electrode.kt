package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.ConnectionType
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import kotlin.random.Random

object ElectrodeConnection : ConnectionType

class Electrode(current: CurrentSignals, val rnd: Random) : SignalsObject(current)