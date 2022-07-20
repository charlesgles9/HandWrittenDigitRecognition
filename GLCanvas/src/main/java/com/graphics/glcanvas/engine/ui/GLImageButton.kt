package com.graphics.glcanvas.engine.ui

import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Font
import com.graphics.glcanvas.engine.structures.Text
import com.graphics.glcanvas.engine.utils.TextureAtlas

class GLImageButton(width:Float, height:Float) : GLView(width, height) {


    constructor(width: Float,height: Float,atlas: TextureAtlas):this(width, height) {
        this.atlas=atlas
        setBackgroundTextureAtlas(atlas)
        setForegroundTextureAtlas(atlas)
    }

     fun setBackgroundImageAtlas(name:String, index:Int){
        setPrimaryImage(name,index)
        setBackgroundSubTexture(name,index)

    }

    fun setBackgroundImageAtlas(name:String){
        setPrimaryImage(name,0)
        setBackgroundSubTexture(name,0)

    }

    fun setText(string:String,font: Font,size:Float){
       text= Text(string,size,font)
       text?.setMaxWidth(width*0.5f)
       text?.setMaxHeight(height)
    }

    fun setTextColor(color: ColorRGBA){
        text?.setColor(color)
    }

    override fun setVisibility(visible: Boolean) {
        super.setVisibility(visible)
    }


}