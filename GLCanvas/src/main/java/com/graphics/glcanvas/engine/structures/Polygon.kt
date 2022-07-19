package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f

class Polygon:Vertex(0,0) {
    private var clipUpper= Vector2f(Float.MIN_VALUE, Float.MIN_VALUE)
    private var clipLower= Vector2f(Float.MAX_VALUE, Float.MAX_VALUE)
    private val paths= MutableList(0,init = { Path(0.0f,0.0f) })
    private var z=0f
    fun moveTo(x:Float,y:Float){
        paths.add(Path(x,y))
    }

    fun lineTo(x:Float,y:Float){
        paths.last().lineTo(x,y)
    }

    fun lineColor(color: ColorRGBA){
        paths.last().setColor(color)
    }
    fun setClipUpper(upperX:Float,upperY:Float){
        clipUpper.set(upperX,upperY)
    }

    fun setClipLower(lowerX:Float,lowerY:Float){
        clipLower.set(lowerX,lowerY)
    }

    fun getClipUpper(): Vector2f {
        return clipUpper
    }

    fun getClipLower(): Vector2f {
        return clipLower
    }

    fun reset(){
        paths.clear()
    }

    fun getPaths():MutableList<Path>{
        return paths
    }

    fun setZ(z:Float){
        this.z=z
    }

    fun getZ():Float{
        return z
    }
}