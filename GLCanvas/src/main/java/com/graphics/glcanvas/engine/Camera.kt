package com.graphics.glcanvas.engine

import android.opengl.Matrix
import com.graphics.glcanvas.engine.maths.Vector3f

open class Camera {

    val Y_UP= Vector3f(0.0f,1.0f,0.0f)
    val ORIGIN= Vector3f(0.0f,0.0f,0.0f)
    // projects the scene to a 2D viewPort
    private val projectionMatrix=FloatArray(16)
    //eye position
    private val eye= Vector3f(0.0f,0.0f,10.0f)
    //we are looking towards the distance
    private val lookAt= Vector3f(ORIGIN)
    // where the head is pointing towards the camera
    private val up= Vector3f(Y_UP)
   
    fun update(mViewMatrix:FloatArray){
        updateView(mViewMatrix)
    }

    private fun updateView(mViewMatrix:FloatArray){
        Matrix.setLookAtM(mViewMatrix,0,eye.x,eye.y,eye.z,
        lookAt.x,lookAt.y,lookAt.z,up.x,up.y,up.z)
    }

    fun getProjectionMatrix():FloatArray{
        return projectionMatrix
    }

    fun getEye():Vector3f{
        return  eye
    }

    fun getLookAt():Vector3f{
        return lookAt
    }

    fun getUp():Vector3f{
        return  up;
    }

};