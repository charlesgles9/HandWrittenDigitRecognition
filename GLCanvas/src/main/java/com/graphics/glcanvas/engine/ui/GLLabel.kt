package com.graphics.glcanvas.engine.ui
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Font
import com.graphics.glcanvas.engine.structures.Text
import com.graphics.glcanvas.engine.utils.TextureAtlas

open class GLLabel(width:Float, height:Float, private var font: Font, private var string:String, private var size: Float) : GLView(width,height) {


    init {

        setText(string,font,size)
        setBackgroundColor(ColorRGBA.transparent)
    }

    constructor(width: Float, height: Float, atlas: TextureAtlas, name: String,index: Int,font:Font,string: String,size: Float):this(width, height,font,string,size) {
        this.atlas=atlas

        this.font=font
        this.string=string
        this.size=size
        setBackgroundTextureAtlas(atlas)
        setBackgroundSubTexture(name,index)
        setBackgroundColor(ColorRGBA.white)
        setText(string,font,size)
    }



    fun setText(string:String, font:Font, size:Float){
        text= Text(string,size,font)
        text?.setMaxWidth(width*0.9f)
        text?.setMaxHeight(height)
    }

    fun setText(string: String){
        text?.setText(string)
    }

    fun setTextColor(color:ColorRGBA){
        text?.setColor(color)
    }

}