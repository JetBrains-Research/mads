package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

data class MocRecord<MO : ModelObject> (val mechanism: ((MO) -> Array<Response>),
                                        val duration: Int,
                                        val condition : ((MO) -> Boolean))
