package com.graphics.glcanvas.engine.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils

class TextureLoader {

    fun getTexture(context: Context,PATH: String):Int{
        return if(!data.containsKey(PATH))
                 loadTexture(context,PATH)
            else
            data[PATH]!!
    }
    fun getTexture(PATH: String):Int{
           return data[PATH]!!
    }
   private fun loadTexture(context: Context,PATH: String):Int{
       val textureHandle=IntArray(1)
        GLES32.glGenTextures(1,textureHandle,0)
        if(textureHandle[0]!=0){
            val options=BitmapFactory.Options()
            options.inScaled=false // no pre-scaling
            // get bitmap from resource loader
            val bitmap:Bitmap?=ResourceLoader().loadBitmapFromAssets(context,PATH)

            // bind the texture to openGL
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,textureHandle[0])
            //filtering
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D,GLES32.GL_TEXTURE_MIN_FILTER,GLES32.GL_NEAREST)
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D,GLES32.GL_TEXTURE_MAG_FILTER,GLES32.GL_NEAREST)
            //load the bitmap into the bound texture
            GLUtils.texImage2D(GLES32.GL_TEXTURE_2D,0,bitmap,0)
            bitmap?.recycle()
        }
        if(textureHandle[0]==0)
            throw RuntimeException("Error loading texture")
        data[PATH] = textureHandle[0]

        return textureHandle[0]
    }

    fun clearTextures(){
        data.clear()
    }

    fun size(){
        data.size
    }

    companion object {
        private var instance:TextureLoader?=null
        private val data= HashMap<String,Int>()
        fun getInstance(): TextureLoader {
            if (instance == null)
                instance = TextureLoader()
            return instance!!
        }
    }

}