package com.graphics.glcanvas.engine.maths

import kotlin.math.cos
import kotlin.math.sin

class Vector3f {

    var x:Float=0.0f
    var y:Float=0.0f
    var z:Float=0.0f

    constructor():this(0.0f,0.0f,0.0f){

    }
    constructor(x:Float,y:Float,z:Float){
       set(x,y,z)
    }
    constructor(other:Vector3f){
       set(other)
    }

    fun set(x:Float,y:Float,z:Float){
        this.x=x
        this.y=y
        this.z=z
    }
    fun set(x:Float,y:Float){
        this.x=x
        this.y=y
    }
    fun set(other: Vector3f){
        this.x=other.x
        this.y=other.y
        this.z=other.z
    }

    fun setValueX(x: Float) {
        this.x=x
    }
    fun setValueY(y: Float) {
        this.y=y
    }
    fun setValueZ(z: Float) {
        this.z=z
    }
    // scalar multiply
    fun multiply(scalar: Float){
        this.x*=scalar
        this.y*=scalar
        this.z*=scalar
    }

    fun multiply(other: Vector3f){
        this.x*=other.x
        this.y*=other.y
    }

    fun add(x:Float,y:Float,z: Float){
        this.x+=x
        this.y+=y
        this.z+=z
    }

    fun add(other: Vector3f){
        this.x+=other.x
        this.y+=other.y
        this.z+=other.z
    }

    fun sub(x:Float,y:Float,z: Float){
        this.x-=x
        this.y-=y
        this.z-=z
    }

    fun sub(other: Vector3f){
        this.x-=other.x
        this.y-=other.y
        this.z-=other.z
    }

    fun rotate(angle:Float):Vector3f{
     val arg=Math.toRadians(angle.toDouble()) .toFloat()
     val a=x
     val b=y
     val x1=a* cos(arg)+b* sin(arg)
     val y1=-1*a* sin(arg)+b* cos(arg)
     return Vector3f(x1,y1,z)
    }
}