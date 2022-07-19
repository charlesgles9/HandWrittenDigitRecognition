package com.graphics.glcanvas.engine.structures

import android.content.Context
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.maths.Vector3f
import com.graphics.glcanvas.engine.utils.SpriteSheet
import com.graphics.glcanvas.engine.utils.Texture
import com.graphics.glcanvas.engine.utils.TextureLoader

open class Vertex(pSize: Int, tSize: Int) {
    private var visible=true
    private var positions: MutableList<Vector3f>? =null
    private var colors:MutableList<ColorRGBA>?=null
    private var scale=Vector2f(1f,1f)
    private var spriteSheet:SpriteSheet?=null
    private var texture=Texture()
    private var angleX=0f
    private var angleY=0f
    private var angleZ=0f
    init {
        positions= MutableList(pSize,init = { Vector3f() })
        colors=MutableList(pSize,init = {ColorRGBA(1.0f,1.0f,1.0f,1.0f) })
        spriteSheet= SpriteSheet(1,1)
    }
    fun setTexture(context: Context, path:String){
        this.texture.load(context, path)
    }
    fun setTexture(texture:Texture){
        this.texture=texture
    }

    fun getTexture():Texture{
        return texture
    }

    fun getSpriteSheet():SpriteSheet{
        return spriteSheet!!
    }

    fun setSpriteSheet(spriteSheet: SpriteSheet?){
        this.spriteSheet=spriteSheet
    }

    fun gradient(start:ColorRGBA, stop:ColorRGBA){
        // must be a square shape
        if(colors!!.size==4){
            val color1=colors!![0]
            val color2=colors!![1]
            val color3=colors!![2]
            val color4=colors!![3]
            color1.set(start)
            color2.set(start)
            color3.set(stop)
            color4.set(stop)
        }
        // else must be a triangle
    }

    fun setColor(color:ColorRGBA){
        colors!!.forEach { it.set(color) }
    }

    fun setScale(sx:Float,sy:Float){
        scale.set(sx,sy)
    }

    fun setVisibility(visible:Boolean){
        this.visible=visible
    }

    fun getVisibility():Boolean{
        return visible
    }

    fun getColor(index:Int):ColorRGBA{
        return colors!![index]
    }

    fun getPosition(index: Int):Vector3f{
        return positions!![index]
    }

    fun getScale():Vector2f{
        return scale
    }
    fun vertexCount():Int{
        return positions!!.size
    }

    fun getTextureCords():FloatArray{
        return spriteSheet!!.getCurrentFrame()
    }

    fun setAngleX(angleX:Float){
        this.angleX=angleX
    }

    fun setAngleY(angleY:Float){
        this.angleY=angleY
    }

    fun setAngleZ(angleZ:Float){
        this.angleZ=angleZ
    }

    fun getAngleX():Float{
        return angleX
    }
    fun getAngleY():Float{
        return angleY
    }
    fun getAngleZ():Float{
        return angleZ
    }
}