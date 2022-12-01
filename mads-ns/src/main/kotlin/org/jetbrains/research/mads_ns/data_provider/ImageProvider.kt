package org.jetbrains.research.mads_ns.data_provider

import java.awt.image.BufferedImage

abstract class ImageProvider(val rootPath: String,
                             val width: Int, val height: Int,
                             val isRandom: Boolean=false) {
    abstract fun getNextImage(): BufferedImage
}