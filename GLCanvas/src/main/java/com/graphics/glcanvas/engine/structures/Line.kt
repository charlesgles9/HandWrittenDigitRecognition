package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.maths.Vector2f

class Line(private var startX: Float, private var startY: Float,
           private var stopX: Float, private var stopY: Float) : Vertex(2, 2) {
    private var clipUpper= Vector2f(Float.MIN_VALUE, Float.MIN_VALUE)
    private var clipLower= Vector2f(Float.MAX_VALUE, Float.MAX_VALUE)
    private var z=0f
    fun set(startX: Float,startY: Float,stopX: Float,stopY: Float){
        this.startX=startX
        this.startY=startY
        this.stopX=stopX
        this.stopY=stopY
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
    fun setStartX(startX: Float){
        this.startX=startX
    }
    fun setStartY(startY: Float){
        this.startY=startY
    }
    fun setStopX(stopX: Float){
        this.stopX=stopX
    }
    fun setStopY(stopY: Float){
        this.stopY=stopY
    }
    fun getStartX():Float{
        return startX
    }
    fun getStartY():Float{
        return startY
    }
    fun getStopX():Float{
        return stopX
    }
    fun getStopY():Float{
        return stopY
    }

    fun setZ(z:Float){
        this.z=z
    }

    fun getZ():Float{
        return z
    }
}