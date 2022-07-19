package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Touch
import com.graphics.glcanvas.engine.maths.Vector2f

class OnClickEvent(private val listener:OnClickListener,
                   private val view:GLView):Touch {

    private var pointerDown=false
    private var position=Vector2f(-1f,-1f)
    fun contains(x:Float,y:Float):Boolean{
        return view.contains(x,y)
    }

    override fun onTouchEvent(event: MotionEvent):Boolean {
         if(event.action ==MotionEvent.ACTION_DOWN){
             position.set(event.x-view.getThumbSize()/2,event.y-view.getThumbSize()/2)
             ScreenRatio.getInstance().project(position)
             pointerDown= contains(position.x,position.y)
                return pointerDown
         }else if(event.action ==MotionEvent.ACTION_UP&&pointerDown){
             position.set(-1f,-1f)
             listener.onClick()
             //for checkboxes
             view.setChecked(!view.getChecked())
             pointerDown=false

         }else if(event.action ==MotionEvent.ACTION_MOVE&&pointerDown){
             position.set(event.x-view.getThumbSize()/2,event.y-view.getThumbSize()/2)
             ScreenRatio.getInstance().project(position)
             pointerDown=contains(position.x,position.y)
             return pointerDown

         }
        return false
    }

    fun getPointerDown():Boolean{
        return pointerDown
    }

    fun getPosition():Vector2f{
        return position
    }

    interface  OnClickListener{
       fun onClick(){

        }
    }
}