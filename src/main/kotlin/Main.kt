//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.apache.commons.net.util.Base64
//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import ru.loginov.color.impl.LabColorSettings
//import ru.loginov.color.impl.lab.LabColor
//import ru.loginov.color.impl.lab.LabColorSpace
//import ru.loginov.figure.Figure
//import ru.loginov.figure.impl.PointFigure
//import ru.loginov.frame.impl.JPanelImageViewer
//import ru.loginov.image.ImagePart
//import ru.loginov.image.impl.DefaultImageManager
//import ru.loginov.image.impl.DefaultImagePart
//import java.io.ByteArrayInputStream
//import java.io.File
//import java.io.FileOutputStream
//import java.nio.file.Files
//import java.util.concurrent.TimeUnit
//import javax.imageio.ImageIO
//import javax.swing.JFrame
//import kotlin.system.measureNanoTime
//
//val logger: Logger = LogManager.getLogger("Main")
//
//fun main(args: Array<String>) {
//    logger.debug("Start process with PID: '{}'", ProcessHandle.current().pid());
//
//    if (args.size < 1) {
//        logger.info("use <path to file> [color different (0 - 1)] [background color square percent (0 - 1)]")
//    } else {
//        val imageFile = File(args[0]);
//        val absolutePathFile = imageFile.absoluteFile
//
//        if (!imageFile.exists()) {
//            throw IllegalArgumentException("Can not read file with path '${absolutePathFile}', because it doesn't exists.")
//        }
//
//        if (imageFile.isDirectory) {
//            throw IllegalArgumentException("Can not read file with path '${absolutePathFile}', because it is directory")
//        }
//
//        val image = ImageIO.read(imageFile);
//        logger.debug("Load image with width: '${image.width}'; height: '${image.height}'")
//
//        val colorSpace = LabColorSpace()
//        val colorSettings = LabColorSettings((if (args.size > 1) args[1].toFloat() else 0.15f), if (args.size > 2) args[2].toFloat() else 0.15f, colorSpace)
//
//        val manager = DefaultImageManager<LabColor>(64, 64, colorSpace, colorSettings, PointFigure::class.java as Class<out Figure<LabColor>>, DefaultImagePart::class.java as Class<out ImagePart<LabColor>>)
//        val initTime = measureNanoTime {
//            manager.init(image.width, image.height, {x, y -> image.getRGB(x, y)}) {now, max ->
//                logger.debug("Init progress $now/$max")
//            }
//        }
//
//        logger.debug("Time for init in ms = ${TimeUnit.MILLISECONDS.convert(initTime, TimeUnit.NANOSECONDS)}")
//
//        var part: ImagePart<LabColor>? = null
//        val time = measureNanoTime {
//            JFrame().apply {
//                val panel = JPanelImageViewer(image, manager)
//                panel.onShow()
//                panel.mouseListeners.forEach {
//                    addMouseListener(it)
//                }
//
//                panel.keyListeners.forEach {
//                    addKeyListener(it)
//                }
//
//                add(panel)
//                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//                isVisible = true
//                setBounds(0,0,800,600)
//            }
//
//        }
//
//        logger.debug("Time for running in ms = ${TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS)}")
//    }
//}
//
//fun main12() {
//    val imageFile = File("./image.jpg");
//    val absolutePathFile = imageFile.absoluteFile
//
//    if (!imageFile.exists()) {
//        throw IllegalArgumentException("Can not read file with path '${absolutePathFile}', because it doesn't exists.")
//    }
//
//    if (imageFile.isDirectory) {
//        throw IllegalArgumentException("Can not read file with path '${absolutePathFile}', because it is directory")
//    }
//
//    val image = ImageIO.read(ByteArrayInputStream(Files.readAllBytes(imageFile.toPath())));
//
//    val encodeBase64 = Base64.encodeBase64String(Files.readAllBytes(imageFile.toPath()))
//
//    val node = ObjectMapper().createObjectNode()
//    node.apply {
//        put("width", image.width)
//        put("height", image.height)
//        put("maxColorDifferent", 0.02f)
//        put("maxBackgroundPercent", 0.2f)
//        put("points", encodeBase64)
//    }
//
//    FileOutputStream(File("./out.json")).use {
//        ObjectMapper().writer().writeValue(it, node)
//    }
//}
