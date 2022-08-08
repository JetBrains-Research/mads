package domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*

open class SimpleObject : ModelObject() {
    override val type = "simple object"
    val forCondition = true
    val rnd = Random(12345L)

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