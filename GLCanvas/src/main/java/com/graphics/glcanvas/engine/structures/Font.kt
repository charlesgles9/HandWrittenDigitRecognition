package com.graphics.glcanvas.engine.structures

import android.content.Context
import java.io.InputStream

class Font(path:String,context: Context) {
  private val metaList= mutableMapOf<Char,FontMetaData>()
   var lineHeight=0f
   var scaleW=0f
   var scaleH=0f
   val padding=FloatArray(4)
   var textureAtlas=""
    companion object {
         val PADDING_LEFT = 0
         val PADDING_TOP = 1
         val PADDING_RIGHT = 2
         val PADDING_BOTTOM = 3
    }
  init {
      val stream:InputStream=context.assets.open(path)
         stream.bufferedReader().forEachLine {
            processLine(it)
         }
  }

  private fun processArgs(str:String):List<String>{
       return str.split("=")
  }

  fun getCharMetaData(char: Char): FontMetaData? {
    return metaList[char]
  }

  private fun processLine(line:String) {
    val array=line.split(" ")
    // it's char data
      if(array[0] == "char"){
           var char='0'
           var x=0f
           var y=0f
           var width=0f
           var height=0f
           var xOffset=0f
           var yOffset=0f
           var xAdvance=0f
          for(i in 1 until array.size){
            val args=processArgs(array[i])
            /* the second argument will always be a number
               in this line segment*/
             if(args.size<2)
                 continue
            val num=args[1].toFloat()
            when(args[0]){
               "id"-> char=num.toInt().toChar()
               "x" -> x=num
               "y"-> y=num
               "width"-> width=num
               "height"-> height=num
               "xoffset"-> xOffset=num
               "yoffset"-> yOffset=num
               "xadvance"-> xAdvance=num
            }
          }
        metaList[char] = FontMetaData(char,x,y,width,height, xOffset, yOffset, xAdvance)
      }else{
        array.forEach {

            when{
                it.indexOf("padding")!=-1->{
                    //format padding=3,3,3,3
                    val pArray=processArgs(it)[1].split(",")
                    padding[PADDING_LEFT]=pArray[0].toFloat()
                    padding[PADDING_TOP]=pArray[1].toFloat()
                    padding[PADDING_RIGHT]=pArray[2].toFloat()
                    padding[PADDING_BOTTOM]=pArray[3].toFloat()
                }
                it.indexOf("lineHeight")!=-1->
                    lineHeight = processArgs(it)[1].toFloat()
                it.indexOf("lineHeight")!=-1->
                    lineHeight = processArgs(it)[1].toFloat()
                it.indexOf("scaleW")!=-1->
                    scaleW = processArgs(it)[1].toFloat()
                it.indexOf("scaleH")!=-1->
                    scaleH = processArgs(it)[1].toFloat()
                it.indexOf("file")!=-1->{
                    textureAtlas=processArgs(it)[1]
                    //remove brackets-> ""
                    textureAtlas=textureAtlas.substring(1,textureAtlas.length-1)
                }

            }
        }
      }
  }

    fun getTextureAtlasPath():String{
        return textureAtlas
    }
}