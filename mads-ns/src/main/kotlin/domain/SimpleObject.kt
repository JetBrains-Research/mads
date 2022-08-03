package domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.ObjectStorage
import org.jetbrains.research.mads.core.types.Response
import java.util.*

open class SimpleObject(storage: ObjectStorage): ModelObject(storage) {
    override val type = "simple object"
    val forCondition = true
    val rnd = Random(12345L)

    init {
        responseMapping[SimpleResponse::class] = ::printResponse
    }

    private fun printResponse(response: Response): Array<ModelObject> {
        if (response is SimpleResponse) {
            println(response.response)
        }

        return arrayOf(this)
    }
}