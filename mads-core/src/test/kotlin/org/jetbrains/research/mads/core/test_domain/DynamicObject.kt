package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Signals

class DynamicObject(vararg signals: Signals) : ModelObject(*signals)