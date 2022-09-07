package org.jetbrains.research.mads_ns.data_provider

import org.apache.commons.collections4.iterators.LoopingIterator
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO



class MnistProvider(rootPath: String, val targetClasses: List<String>, isRandom: Boolean=false) :
        ImageProvider(rootPath, height=28, width=28, isRandom=isRandom) {
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
        return img
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