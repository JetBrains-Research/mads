package org.jetbrains.research.mads_ns.types.responses

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.awt.image.BufferedImage

data class ArrayStimuliResponse (
        override val response: String,
        override val sourceObject: ModelObject,
        override val logFunction: (Long, Response) -> Response,
        override val logResponse: Boolean,
        val stimuliData: BufferedImage
        ) : Response