//package ru.loginov.frame.impl
//
//import org.apache.logging.log4j.LogManager
//import ru.loginov.color.impl.lab.LabColor
//import ru.loginov.figure.Figure
//import ru.loginov.frame.ImageViewer
//import ru.loginov.graph.impl.PointGraph
//import ru.loginov.image.ImageManager
//import ru.loginov.image.ImagePart
//import java.awt.*
//import java.awt.event.KeyEvent
//import java.awt.event.KeyListener
//import java.awt.event.MouseAdapter
//import java.awt.event.MouseEvent
//import java.awt.image.BufferedImage
//import java.io.File
//import java.io.FileOutputStream
//import java.io.OutputStream
//import java.security.SecureRandom
//import java.util.*
//import javax.imageio.ImageIO
//import javax.swing.JFrame
//import javax.swing.JPanel
//import kotlin.collections.ArrayList
//import kotlin.streams.asStream
//import kotlin.streams.toList
//
//class JPanelImageViewer(override var image: BufferedImage?, override var manager: ImageManager<LabColor>?) : JPanel(), ImageViewer<LabColor> {
//
//    private val logger = LogManager.getLogger("JFrameImageViewer")
//
//    private var part: ImagePart<LabColor>? = null
//    private val selectedFigures: MutableList<Figure<LabColor>> = ArrayList()
//
//    init {
//        addMouseListener(object: MouseAdapter() {
//            override fun mouseReleased(e: MouseEvent?) {}
//
//            override fun mouseEntered(e: MouseEvent?) {}
//
//            override fun mouseClicked(e: MouseEvent?) {}
//
//            override fun mouseExited(e: MouseEvent?) {}
//
//            override fun mousePressed(e: MouseEvent?) {
//                if (e != null && !e.isPopupTrigger) {
//                    onClick(e.x, e.y)
//                }
//            }
//        })
//
//        addKeyListener(object : KeyListener {
//            override fun keyTyped(e: KeyEvent?) {}
//
//            override fun keyPressed(e: KeyEvent?) {
//
//                if (e != null && e.isControlDown && manager != null) {
//                    when (e.keyCode) {
//                        KeyEvent.VK_S -> {
//                            logger.debug("Start saving frame's image")
//                            val dialog = FileDialog(null as Frame?, "Select file to save")
//                            dialog.mode = FileDialog.LOAD
//                            dialog.isVisible = true
//
//                            val filePathInString = dialog.file
//                            if (filePathInString != null) {
//                                val file = File(filePathInString)
//                                if (file.exists() || file.createNewFile()) {
//                                    FileOutputStream(file).use {
//                                        onSave(it)
//                                        logger.info("Saved frame's image to file following by path: ${file.absolutePath}")
//                                    }
//                                } else {
//                                    logger.warn("Can not find or create file")
//                                }
//                            } else {
//                                logger.debug("Abort saving frame's image")
//                            }
//                        }
//                        KeyEvent.VK_D -> {
//                            logger.debug("Start show diagram")
//                            var index = 0
//
//                            selectedFigures.sortBy { it.getPoints().keys.minBy { p -> p.first }!!.first }
//
//                            val averagesColors = selectedFigures.map {
//                                var averageColor: LabColor? = null
//                                for (color in it.getPoints().values) {
//                                    averageColor = if (averageColor == null) color else manager!!.colorSpace.average(averageColor, color)
//                                }
//
//                                averageColor
//                            }
//
//                            val random = SecureRandom()
//
//                            val diagram = PointGraph(if (averagesColors.size < 1000) {
//                                generateSequence { random.nextDouble() % 5000 }.distinct().asStream()
//                            } else {
//                                generateSequence { random.nextDouble() }.distinct().asStream()
//                            }
//                                    .limit(averagesColors.size.toLong()).map{it!! to averagesColors[index++]!!.components[0]}.toList().toTypedArray())
//
//                            val diagramFrameWidth = 800
//                            val diagramFrameHeight = 600
//                            val diagramFrame = object : JFrame() {
//                                override fun paint(g: Graphics?) {
//
//                                    val g2 = g!!
//                                    val readHeight = height
//                                    val realWidth = width - 16
//
//                                    g2.color = Color.RED
//                                    val verticalScale = readHeight / (diagram.maxY - diagram.minY)
//                                    val horizontalStep = (diagram.maxX - diagram.minX) / realWidth
//                                    var i = diagram.minX
//                                    var lastRealX = 0
//                                    var lastRealY = ((diagram.getY(i) - diagram.minY) * verticalScale).toInt()
//                                    i += horizontalStep
//                                    while (i <= diagram.maxX) {
//                                        val realY = ((diagram.getY(i) - diagram.minY) * verticalScale).toInt()
//                                        g2.drawLine(8 + lastRealX++, readHeight - lastRealY + 16, 8 + lastRealX, readHeight - realY + 16)
//                                        i += horizontalStep
//                                        lastRealY = realY
//                                    }
//                                }
//                            }
//                            diagramFrame.setBounds(0, 0, diagramFrameWidth, diagramFrameHeight)
//                            diagramFrame.isVisible = true
//                        }
//                    }
//                }
//            }
//
//            override fun keyReleased(e: KeyEvent?) {}
//        })
//
//        isVisible = false
//    }
//
//    override fun paint(g: Graphics?) {
//        if (image == null) {
//            throw IllegalStateException("Image is not init")
//        }
//
//        if (part == null) {
//            throw IllegalStateException("Part is not init")
//        }
//
//        val newImage = BufferedImage(image!!.width, image!!.height, BufferedImage.TYPE_INT_ARGB)
//        val imageGraphics = newImage.graphics
//        imageGraphics.drawImage(image, 0, 0, null)
//
//        part!!.getFigures().forEach {
//            if (selectedFigures.contains(it)) {
//                imageGraphics.color = Color(0, 255,  0, 200)
//            } else {
//                imageGraphics.color = Color(Random().nextInt())
//            }
//            it.getPoints().forEach { (point, _) -> imageGraphics.drawRect(point.first, point.second, 1, 1) }
//        }
//
//        g?.drawImage(newImage, 0, 0, this.width, this.height, null)
//    }
//
//    override fun onShow() {
//        part = manager?.run(){now, max ->
//            logger.debug("Init progress $now/$max")
//        }
//
//        if (part == null) {
//            logger.error("Can not show viewer because manager is null or manager return null after running")
//        }
//
//        val width = image!!.width + 8;
//        val height = image!!.height + 8
//        val x = Toolkit.getDefaultToolkit().screenSize.width / 2 - width
//        val y = Toolkit.getDefaultToolkit().screenSize.height / 2 - height
//
//        bounds = Rectangle(x, y, width, height)
//        isVisible = true
//    }
//
//    override fun onSave(output: OutputStream) {
//        val image = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB)
//        val graphics = image.createGraphics()
//        this.paintAll(graphics);
//        ImageIO.write(image, "png", output)
//    }
//
//    override fun onClick(x: Int, y: Int) {
//        val _x = (x * (image!!.width.toDouble() / (width - 8))).toInt()
//        val _y = (y * (image!!.height.toDouble() / (height - 8))).toInt()
//        part?.getFigures()?.filter { it.containsPoint(_x, _y) }?.forEach {
//            if (selectedFigures.contains(it)) {
//                selectedFigures.remove(it)
//            } else {
//                selectedFigures.add(it)
//            }
//        }
//        repaint()
//    }
//}