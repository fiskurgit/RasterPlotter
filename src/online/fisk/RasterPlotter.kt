package online.fisk

import java.io.File

fun main(args: Array<String>) {
    if(args.isEmpty() || args[0] == "help"){
        RasterPlotter().showHelp()
    }else{
        val threshold = when {
            args.size >= 2 -> args[1].toInt()
            else -> 1
        }
        val isochronal = when {
            args.size == 3 && args[2] == "-iso" -> true
                else -> false
        }
        RasterPlotter().process(args[0], threshold, isochronal)
    }
}

class RasterPlotter {

    companion object {
        fun out(message: String){
            System.out.println(message)
        }
    }

    fun showHelp(){
        out("RasterPlotter - Help")
        out("Usage: RasterPlotter pathToImage thresholdValue")
    }

    fun process(source: String, threshold: Int, isochronal: Boolean){
        out("RasterPlotter - processing...")

        //Invalid source file will exit
        val file = File(source)
        validateFile(source, file)

        RasterToSvg.process(file, threshold, isochronal, callback = { filename ->
            out("SVG processing finished:")
            out(filename)
        })
    }

    private fun validateFile(source: String, file: File){
        if(!file.exists()){
            out("Error: $source does not exist")
            System.exit(-1)
        }else{
            out("Source: $source")
        }
    }
}