package domain.objects

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class DynamicObject(override val signals: Signals) : SignalsObject(signals)