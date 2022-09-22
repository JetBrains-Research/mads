package org.jetbrains.research.mads_ns.data_provider

import org.apache.commons.collections4.iterators.LoopingIterator
import java.awt.RenderingHints
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO


class MnistProvider(rootPath: String, val targetClasses: List<String>, isRandom: Boolean=false) :
        ImageProvider(rootPath, height=10, width=10, isRandom=isRandom) {
    private var classFileMaps: HashMap<String, Iterator<File>> = HashMap()
    private val classIterator = LoopingIterator(classFileMaps.keys)
    init {
        targetClasses.forEach {
            val classWalker = getClassIterator(it)
            classFileMaps[it] = classWalker
        }
    }

    override fun getNextImage() : BufferedImage
    {
        val currentClass = if (isRandom) "0" else classIterator.next()

        if(!classFileMaps[currentClass]!!.hasNext())
        {
            classFileMaps[currentClass] = getClassIterator(currentClass)
        }

        val imagePathIterator = classFileMaps[currentClass]
        val imagePath = imagePathIterator?.next()

        val img = ImageIO.read(File(imagePath!!.toURI()))


        return resize(img, 10)
    }

    private fun resize(src: BufferedImage, targetSize: Int): BufferedImage {
        if (targetSize <= 0) {
            return src //this can't be resized
        }
        var targetWidth = targetSize
        var targetHeight = targetSize
        val ratio = src.height.toFloat() / src.width.toFloat()
        if (ratio <= 1) { //square or landscape-oriented image
            targetHeight = Math.ceil((targetWidth.toFloat() * ratio).toDouble()).toInt()
        } else { //portrait image
            targetWidth = Math.round(targetHeight.toFloat() / ratio)
        }
        val bi = BufferedImage(targetWidth, targetHeight, if (src.transparency == Transparency.OPAQUE) BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB)
        val g2d = bi.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR) //produces a balanced resizing (fast and decent quality)
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null)
        g2d.dispose()
        return bi
    }

    private fun getClassIterator(classLabel: String): Iterator<File>
    {
        val classPath = Paths.get(rootPath, classLabel)
        val classIterator = File(classPath.toUri()).listFiles()?.iterator()

        if(classIterator == null)
        {
            val exceptionString = String.format("Cannot get files for class: %s", classLabel)
            throw RuntimeException(exceptionString)
        }

        return classIterator
    }
}