package domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

class DummyObject: ModelObject() {
    override val type = "dummy object"
    val forCondition = true

    init {
        responseMapping[SimpleResponse::class] = ::printResponse
    }

    override fun resolveConflicts(responses: List<Response>): List<Response> {
        return responses
    }

    private fun printResponse(response: Response): List<ModelObject> {
        if (response is SimpleResponse) {
            println(response.response)
        }

        return arrayListOf(this)
    }
}