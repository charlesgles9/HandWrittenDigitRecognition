package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector3f

class Path(x:Float,y:Float) {

    private val endPoints= MutableList(0,init = { Vector3f() })
    private val start=Vector3f(x,y,0.0f)
    // add start color
    private val colors= MutableList(1, init={ColorRGBA()})

    fun moveTo(x:Float,y:Float){
        start.setValueX(x)
        start.setValueY(y)
    }

    fun lineTo(x:Float,y:Float){
        endPoints.add(Vector3f(x,y,0.0f))
        colors.add(ColorRGBA())
    }

    fun getStart():Vector3f{
        return start
    }

    fun setColor(r:Float,g:Float,b:Float,a:Float){
        colors.forEach { color->
            color.set(r,g,b,a)
        }
    }

    fun setColor(color: ColorRGBA){
        colors.forEach {
            it.set(color)
        }
    }

    fun getColor(index:Int):ColorRGBA{
        return colors[index]
    }

    fun getEndPoints():MutableList<Vector3f>{
        return endPoints
    }

    fun getColors():MutableList<ColorRGBA>{
        return colors
    }
}