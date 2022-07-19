package com.graphics.glcanvas.engine.maths

import kotlin.math.sqrt

class Vector2f {

    var x:Float= 0.0f
    var y:Float=0.0f
     constructor(){}
     constructor(x:Float,y:Float){
         this.x=x
         this.y=y
     }

     fun add(otherX:Float, otherY:Float){
       this.x+=otherX
       this.y+=otherY
    }

    fun add(other:Vector2f){
        this.x+=other.x
        this.y+=other.y
    }


    fun addX(otherX: Float){
        this.x+=otherX
    }

    fun addY(otherY: Float){
        this.y=otherY
    }

    fun sub(other:Float){
        this.x-=other
        this.y-=other
    }
    fun sub(otherX:Float, otherY:Float){
        this.x-=otherX
        this.y-=otherY
    }

    fun sub(other:Vector2f){
        this.x-=other.x
        this.y-=other.y
    }
    fun multiply(other:Float){
        this.x*=other
        this.y*=other
    }
    fun multiply(other:Vector2f){
        this.x*=other.x
        this.y*=other.y
    }
    // useful for scale
    fun multiply(otherX:Float, otherY:Float){
        this.x*=otherX
        this.y*=otherY
    }
    // useful for behaviors such as friction
    fun divide(magnitude:Float){
        if(magnitude!=0.0f) {
            this.x /= magnitude
            this.y /= magnitude
        }
    }

    fun set(other:Vector2f){
        this.x=other.x
        this.y=other.y
    }

    fun set(otherX:Float, otherY:Float){
        this.x=otherX
        this.y=otherY
    }

    fun dotProduct(other: Vector2f):Float{
        return(x*other.x)+(y*other.y)
    }

    fun length(): Float {
        return sqrt((x*x+y*y).toDouble()).toFloat()
    }

     fun distance(other:Vector2f):Float{
        val dx=x-other.x
        val dy=y-other.y
        return sqrt(dx*dx+dy*dy)
    }

    fun print(){
        println("x: $x  y: $y")
    }

}