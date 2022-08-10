package domain.objects

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class DynamicObject(vararg signals: Signals) : SignalsObject(*signals)