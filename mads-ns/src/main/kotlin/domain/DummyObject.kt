package domain

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.ObjectStorage
import org.jetbrains.research.mads.core.types.Response

class DummyObject(storage: ObjectStorage) : ModelObject(storage) {
    override val type = "dummy object"
    val forCondition = true

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