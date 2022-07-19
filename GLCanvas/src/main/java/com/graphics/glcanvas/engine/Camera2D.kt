package com.graphics.glcanvas.engine

import android.opengl.Matrix
import com.graphics.glcanvas.engine.maths.Vector2f

class Camera2D(height: Float) : Camera() {

    init {
        setHeight2D(height)
    }


    private fun setHeight2D(height: Float){
        getEye().setValueZ(height)
    }

    fun setPosition2D(x:Float, y:Float){
         setEyePosition2D(x,y)
         setLookAtPosition2D(x,y)
    }

    fun setPosition2D(vec: Vector2f){
        setEyePosition2D(vec.x,vec.y)
        setLookAtPosition2D(vec.x,vec.y)
    }

    private fun setEyePosition2D(x:Float, y:Float){
        getEye().set(x,y,getEye().z)
    }

    private fun setLookAtPosition2D(x:Float, y:Float){
        getLookAt().set(x,y,getLookAt().z)
    }



    fun setOrtho(width:Float,height: Float){
        val ratio=width/height
        val left=0.0f
        val top=0.0f
        val near=0.0f
        val far=10.0f
        Matrix.orthoM(getProjectionMatrix(),0,left, width, height,top,near, far)
    }
}