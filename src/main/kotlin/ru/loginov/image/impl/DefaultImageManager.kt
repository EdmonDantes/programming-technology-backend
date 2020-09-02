package ru.loginov.image.impl

import org.apache.commons.lang3.tuple.MutablePair
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import ru.loginov.color.ColorSettings
import ru.loginov.color.CustomColorSpace
import ru.loginov.comparator.PairComparator
import ru.loginov.figure.Figure
import ru.loginov.image.ImageManager
import ru.loginov.image.ImagePart
import java.lang.reflect.Constructor
import java.util.LinkedList
import java.util.TreeMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.stream.Collectors
import kotlin.math.min
import kotlin.system.measureTimeMillis

class DefaultImageManager<T>(override val maxWidthImagePart: Int,
                          override val maxHeightImagePart: Int,
                          override val colorSpace: CustomColorSpace<T>,
                          override val colorSettings: ColorSettings<T>,
                          override val figureClass: Class<out Figure<T>>,
                          imagePartClass: Class<out ImagePart<T>>)
    : ImageManager<T> {

    private val logger: Logger = LogManager.getLogger("ImageManager")

    companion object {
        private val companionLogger = LogManager.getLogger("ImageManager:CompanionObject")
        private var threadCount: Int = 4
        val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)

        init {
            companionLogger.debug("Create companion object with thread count = {}", threadCount)
        }
    }

    private val parts: MutableList<MutableList<Pair<ImagePart<T>, MutableMap<Pair<Int, Int>, T>>>> = ArrayList()
    private val figureConstructor: Constructor<out Figure<T>>
    private val imagePartConstructor: Constructor<out ImagePart<T>>
    override val backgroundColors: MutableCollection<T> = ArrayList()

    init {
        try {
            figureConstructor = figureClass.getConstructor(ColorSettings::class.java, CustomColorSpace::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Can not get special constructor for figure class: $figureClass", e)
        }

        try {
            imagePartConstructor = imagePartClass.getConstructor(Int::class.java, Int::class.java, Int::class.java, Int::class.java, ImageManager::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Can not get special constructor with arguments (Int, Int, Int, Int, IImageManager) from image part class: $imagePartClass", e)
        }

        logger.debug("Created image manage with max width = {} and max height = {}. Figure class: {}. ImagePart class: {}",
            maxWidthImagePart, maxHeightImagePart, figureClass, imagePartClass)
    }

    override fun createFigure(): Figure<T> = figureConstructor.newInstance(colorSettings, colorSpace)

    override fun init(width: Int, height: Int, pointGetter: (Int, Int) -> Int, progressFunc: (Int, Int) -> Unit) {
        val lineCount = height / maxHeightImagePart + if (height % maxHeightImagePart != 0) 1 else 0
        val lineLength = width / maxWidthImagePart + if (width % maxWidthImagePart != 0) 1 else 0

        val colors = ArrayList<MutablePair<T, Int>>()
        val futures = LinkedList<Future<LinkedList<MutablePair<T, Int>>>>()


        for (yIndex in 0 until lineCount) {
            val line: MutableList<Pair<ImagePart<T>, MutableMap<Pair<Int, Int>, T>>> = ArrayList(lineLength + 1)
            parts.add(line)
            for (xIndex in 0 until lineLength) {
                val partPoints = TreeMap<Pair<Int,Int>, T>(PairComparator())

                line.add(createImagePart(
                        xIndex * maxWidthImagePart,
                        yIndex * maxHeightImagePart,
                        min(maxWidthImagePart, width - xIndex * maxWidthImagePart),
                        min(maxHeightImagePart, height - yIndex * maxHeightImagePart)) to partPoints)

                futures.add(executor.submit<LinkedList<MutablePair<T, Int>>> {
                    val localColors = LinkedList<MutablePair<T, Int>>()
                    var x = xIndex * maxWidthImagePart
                    while (x < x + maxWidthImagePart && x < width) {
                        var y = yIndex * maxHeightImagePart
                        while (y < y + maxHeightImagePart && y < height) {
                            val pixelColor = colorSpace.fromRGB(pointGetter(x, y))
                            //var pixelColor = colorSpace.fromCustom(Color(pointGetter(x, y)))
                            partPoints[x to y] = pixelColor

                            var findColor = false

                            for (pair in localColors) {
                                if (colorSettings.equalsColors(pair.key, pixelColor)) {
                                    pair.setValue(pair.value + 1)
                                    findColor = true
                                    break
                                }
                            }

                            if (!findColor) {
                                localColors.add(MutablePair(pixelColor, 1))
                            }

                            y++
                        }
                        x++
                    }

                    localColors
                })
            }
        }


//        var progress = 0;
//        var index = 0;
//        for (x in 0 until width) {
//            val xIndex = x / maxWidthImagePart
//            for (y in 0 until height) {
//                val yIndex = y / maxHeightImagePart
//
//                while (parts.size <= yIndex) {
//                    parts.add(ArrayList(lineLength))
//                }
//
//                val line = parts[yIndex]
//                if (line.size <= xIndex) {
//                    line.add(createImagePart(x, y, min(maxWidthImagePart, width - x), min(maxHeightImagePart, height - y)) to TreeMap(PairComparator()))
//                }
//
//                progress++;
//                val pixelColor = pointGetter.invoke(x, y)
//                if (pixelColor != null) {
//                    val color = colorSpace.fromCustom(Color(pixelColor))
//                    line[xIndex].second[x to y] = color
//
//
//                    var findColor = false
//
//                    for (entity in colors) {
//                        if (colorSettings.equalsColors(entity.key, color)) {
//                            entity.setValue(entity.value + 1)
//                            findColor = true
//                        }
//                    }
//
//                    if (!findColor) {
//                        colors.add(MutablePair(color, 1))
//                    }
//                }
//                while (progress * 100 / (width * height) > index) {
//                    index++
//                    logger.info("Progress $index%")
//                }
//
//            }
//        }

        var index = 0;
        val futureSize = futures.size
        progressFunc(index, futureSize)
        futures.forEach {
//            while (!it.isDone && !it.isCancelled) {
//                Thread.yield()
//            }

            val localColors = it.get()

            for (tmpColor in localColors) {
                var notFindColor = true;
                for (color in colors) {
                    if (colorSettings.equalsColors(tmpColor.left, color.left)) {
                        color.setValue(color.value + tmpColor.right);
                        notFindColor = false;
                        break;
                    }
                }

                if (notFindColor) {
                    colors.add(tmpColor);
                }

            }

            progressFunc(++index, futureSize);
        }

        backgroundColors.addAll(colors
                .sortedBy { -it.right }
                .stream()
                .filter { colorSettings.isBackground(width * height, it.value) }
                .map { it.key }
                .collect(Collectors.toList()))

        logger.debug("Image manager was init by image with width: '{}' and height: '{}'", width, height)
        logger.trace("Image colors:\n{}", colors)
        logger.trace("Background colors:\n{}", backgroundColors)
    }

    override fun run(progressFunc: (Int, Int) -> Unit): ImagePart<T>? {
        logger.debug("Image manager will be run")

        val futures = LinkedList<Future<*>>()

        var index = 0;
        var futureSize: Int

        parts.forEach { row ->
            row.forEach { pair ->
                futures.add(executor.submit {
                    pair.second.forEach {
                        pair.first.addPoint(it.key.first, it.key.second, it.value)
                    }
                })
            }
        }

        futureSize = futures.size;
        progressFunc(index, futureSize)
        val findTime = measureTimeMillis {
            futures.forEach {
                while (!it.isDone && !it.isCancelled) {
                    Thread.yield()
                }
                progressFunc(++index, futureSize)
            }
        }

        logger.debug("Time for find figures: $findTime")

        futures.clear()
        parts.forEach { row ->
            futures.add(executor.submit {
                if (row.isNotEmpty()) {
                    var line: ImagePart<T>? = row[0].first
                    for (i in 1 until row.size) {
                        line = line!!.mesh(row[i].first)
                    }
                    row.clear()
                    if (line != null) {
                        row.add(line to TreeMap(PairComparator()))
                    }
                }
            })
        }

        futureSize += futures.size

        progressFunc(index, futureSize)
        val meshTime = measureTimeMillis {
            futures.forEach {
                while (!it.isDone && !it.isCancelled) {
                    Thread.yield()
                }

                progressFunc(++index, futureSize)
            }
        }
        logger.debug("Time for mesh rows: $meshTime")

        futures.clear()

        progressFunc(index, ++futureSize )
        var imagePart: ImagePart<T>? = null
        val meshLineTime = measureTimeMillis {
            parts.forEach { row ->
                if (row.size == 1) {
                    imagePart = if (imagePart != null) imagePart!!.mesh(row[0].first) else row[0].first
                }
            }
        }

        progressFunc(++index, futureSize)

        logger.debug("Time for mesh lines: $meshLineTime")

        logger.debug("Image part ended execution")

        return imagePart
    }

    private fun createImagePart(x: Int, y: Int, width: Int, height: Int): ImagePart<T> = imagePartConstructor.newInstance(x, y, width, height, this)
}