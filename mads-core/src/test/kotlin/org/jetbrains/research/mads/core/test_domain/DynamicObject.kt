package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject

class DynamicObject(vararg signals: Signals) : SignalsObject(*signals)