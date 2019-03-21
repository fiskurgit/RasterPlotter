package online.fisk

import java.awt.image.BufferedImage
import java.io.File
import java.io.FileWriter
import javax.imageio.ImageIO
import kotlin.concurrent.thread

object RasterToSvg {

    const val HEX_BLACK = "00000000"
    const val HEX_WHITE = "ffffffff"

    /**

        blockThreshold doesn't take black pixels into account which would be better, it just discards white lines less than the given threshold to speed up plotting
        It might be more effective to evaluate a high entropy BWBWBBBWBWBWWBBWWW row of pixels get the average and decide whether to draw as white or ignore.
        Some function to look ahead until entropy/average val (where BW = 0.5, BB = 0, WW = 1) drops > or <  of some range.

     */

    fun process(imageFile: File, blockThreshold: Int, isochronal: Boolean, callback: (svgFilename: String) -> Unit){

        val lines = mutableListOf<Line>()
        val source: BufferedImage = ImageIO.read(imageFile)

        thread {
            val width = source.width
            val height = source.height

            var line = Line()
            var drawingLine: Boolean

            for (y in 0 until height) {

                if(line.length >= blockThreshold){
                    lines.add(line)
                }

                line = Line()
                drawingLine = false

                for (x in 0 until width) {

                    val pixel = source.getRGB(x, y)

                    if(Integer.toHexString(pixel) == HEX_WHITE){
                        when {
                            drawingLine -> line.increment()
                            else -> {
                                if (line.length >= blockThreshold) {
                                    lines.add(line)
                                }

                                line = Line()

                                line.index = y
                                line.startX = x
                                line.startY = y
                                drawingLine = true
                            }
                        }
                    }else{
                        //White - line end
                        drawingLine = false
                    }
                }
            }

            val filename = "${imageFile.nameWithoutExtension}.svg"
            outputToFile(filename, lines, width, height, isochronal)
            callback(filename)
        }
    }

    private fun outputToFile(filename: String, lines: List<Line>, width: Int, height: Int, isochronal: Boolean){
        val writer = FileWriter(File(filename))

        writer.appendln("<svg version=\"1.1\"\n" +
                "     baseProfile=\"full\"\n" +
                "     width=\"$width\" height=\"$height\"\n" +
                "     xmlns=\"http://www.w3.org/2000/svg\">")

        lines.forEach {
            //If isochronal draw ever other line - easier on the pen plotter and less ink bleed
            if (isochronal && it.index % 2 != 0) {
            } else {
                writer.appendln("<line x1=\"${it.startX}\" x2=\"${it.startX + it.length}\" y1=\"${it.startY}\" y2=\"${it.startY}\" stroke=\"black\" stroke-width=\"1\"/>")
            }
        }

        writer.appendln("</svg>")

        writer.close()
    }

    class Line{
        var index = 0
        var startX: Int = 0
        var startY: Int = 0

        var length = 0

        fun increment(){
            length++
        }
    }
}