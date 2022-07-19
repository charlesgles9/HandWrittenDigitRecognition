package com.graphics.glcanvas.engine.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class ResourceLoader {


   fun loadTextFromAssets(context: Context, path: String):String{
       val assets=context.assets
       val stream=assets.open(path)
       var content=""
       try {
           val size = stream.available()
           val buffer = ByteArray(size)
           stream.read(buffer)
           stream.close()
           content = String(buffer, Charset.forName("UTF-8"))
       }catch (io:IOException){

           println("Error: "+io.message)
       }
       return  content
   }

   fun  loadBitmapFromAssets(context: Context, path:String): Bitmap? {
        var bitmap:Bitmap?=null
        val assets=context.assets
        var stream:InputStream?=null
        try {
            stream=assets.open(path)
            val options=BitmapFactory.Options()
            options.inScaled=false
            bitmap=BitmapFactory.decodeStream(stream,null,options)
        }catch (e:IOException){
            return bitmap
        } finally {
            try {
                stream?.close()
            }catch (e:IOException){}
        }
        return bitmap
    }
}