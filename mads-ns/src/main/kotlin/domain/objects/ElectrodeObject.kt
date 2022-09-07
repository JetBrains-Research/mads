package domain.objects

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class ElectrodeObject(vararg sig: Signals) : SignalsObject(*sig) {
    var isStimulating = false
}