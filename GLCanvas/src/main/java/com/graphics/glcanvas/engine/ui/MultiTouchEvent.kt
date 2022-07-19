package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Touch
import com.graphics.glcanvas.engine.maths.AxisABB
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.RectF

open class MultiTouchEvent(private val listener:OnMultiTouchListener, private val rect: RectF) :Touch{

    private val fingers=HashMap<String,Vector2f>()
    private var axisABB=AxisABB()
    private var thumb=30f
    fun contains(x:Float,y:Float):Boolean{
        return axisABB.
        isIntersecting(rect.getX(),
                      rect.getY(),rect.getWidth(),rect.getHeight(),x,y,thumb,thumb)
    }

    fun getTouchDown():Boolean{
        var touched=false
        try {
            for (vector in fingers.entries) {
                touched = touched.or(contains(vector.value.x, vector.value.y))
            }
        }catch (e:ConcurrentModificationException){

        }
        return touched
    }

    private fun project(vec:Vector2f){
        ScreenRatio.getInstance().project(vec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      val action=event.actionMasked
              val i=event.actionIndex
              val x=event.getX(i)-thumb*0.5f
              val y=event.getY(i)-thumb*0.5f
              val vector=Vector2f(x,y)
                  project(vector)
             when (action) {
                 MotionEvent.ACTION_DOWN -> {
                     fingers["Id$i"] = vector
                     if(contains(vector.x,vector.y))
                      listener.onTouch(vector)
                 }
                 MotionEvent.ACTION_UP -> {
                     // no finger is touching
                     fingers.clear()
                     listener.onRelease()
                 }
                 MotionEvent.ACTION_MOVE-> {
                     fingers["Id$i"] = vector
                     if(contains(vector.x,vector.y))
                      listener.onTouch(vector)
                 }
                 MotionEvent.ACTION_POINTER_UP-> {
                     fingers.remove("Id$i")
                 }
                 MotionEvent.ACTION_POINTER_DOWN -> {
                     fingers["Id$i"] = vector
                     if(contains(vector.x,vector.y))
                     listener.onTouch(vector)
                 }
             }

        return true
    }
    interface OnMultiTouchListener{
        fun onTouch(vector2f: Vector2f)
        fun onRelease()

    }
}