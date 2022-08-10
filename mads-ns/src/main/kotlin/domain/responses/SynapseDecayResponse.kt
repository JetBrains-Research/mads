package domain.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

class SynapseDecayResponse(override val response: String, override val sourceObject: ModelObject) : Response {
}