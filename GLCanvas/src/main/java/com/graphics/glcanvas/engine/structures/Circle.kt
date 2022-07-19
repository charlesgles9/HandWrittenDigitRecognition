package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.maths.Vector3f

class Circle:Vertex {
    private val position= Vector3f()
    private var clipUpper= Vector2f(Float.MIN_VALUE, Float.MIN_VALUE)
    private var clipLower= Vector2f(Float.MAX_VALUE, Float.MAX_VALUE)
    private var width=0.0f
    private var height=0.0f
    private var thickness=0.0f
    constructor():super(4,4){}
    constructor(x:Float,y:Float,radius:Float):super(4,4){
        this.width=radius
        this.height=radius
        this.position.set(x,y,0.0f)
    }

    operator fun set(x:Float, y:Float){
        position.setValueX(x)
        position.setValueY(y)
    }

    fun setZ(z:Float){
        this.position.z=z
    }

    fun getX():Float{
        return position.x
    }

    fun getY():Float{
        return position.y
    }

    open fun getZ():Float{
        return position.z
    }

    fun setClipUpper(upperX:Float,upperY:Float){
        clipUpper.set(upperX,upperY)
    }

    fun setClipLower(lowerX:Float,lowerY:Float){
        clipLower.set(lowerX,lowerY)
    }

    fun getClipUpper():Vector2f{
        return clipUpper
    }

    fun getClipLower():Vector2f{
        return clipLower
    }
   fun getRadius():Float{
       return width
   }

   fun setRadius(radius: Float){
        this.width=radius
        this.height=radius

    }

   fun setThickness(thickness:Float){
       this.thickness=thickness
   }

    fun getThickness():Float{
        return thickness
    }
}