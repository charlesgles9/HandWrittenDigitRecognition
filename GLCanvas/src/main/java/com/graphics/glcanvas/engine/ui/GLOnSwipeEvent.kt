package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Touch
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.utils.FpsCounter
import kotlin.math.abs

class GLOnSwipeEvent (private val listener: OnSwipeListener,
                      private val view:GLView): Touch {
    private val threshHold=3.8f
    private val velocity=Vector2f(0f,0f)
    private var origin=Vector2f(-1f,-1f)
    private var move=Vector2f(-1f,-1f)
    private var offset=Vector2f()
    private var maxOffset=Vector2f()
    private var minOffset=Vector2f()
     var UP=false
     var DOWN=false
     var LEFT=false
     var RIGHT=false
    companion object {
        var friction = 0.1f
    }
    private var pointerDown=false
    fun contains(x:Float,y:Float):Boolean{
        return view.contains(x,y)
    }

    fun getVelocity():Vector2f{
        return velocity
    }
    fun getPointerDown():Boolean{
        return pointerDown
    }
    fun setVelocity(x:Float,y:Float){
        this.velocity.set(x,y)
    }

    fun setOffset(offset:Vector2f){
        this.offset=offset
    }

    fun setMaxOffset(x:Float,y:Float){
        this.maxOffset.set(x,y)
    }

    fun setMinOffset(x:Float,y:Float){
        this.minOffset.set(x,y)
    }

    override fun onTouchEvent(event: MotionEvent):Boolean {
        if(event.action ==MotionEvent.ACTION_DOWN){
            origin.set(event.x-view.getThumbSize()/2,event.y-view.getThumbSize()/2)
            ScreenRatio.getInstance().project(origin)
            pointerDown= contains(origin.x,origin.y)

        }else if(event.action ==MotionEvent.ACTION_UP&&pointerDown){
            origin.set(-1f,-1f)
            pointerDown=false
        }else if(event.action ==MotionEvent.ACTION_MOVE&&pointerDown){
            move.set(event.x-view.getThumbSize()/2,event.y-view.getThumbSize()/2)
            ScreenRatio.getInstance().project(move)
            pointerDown=contains(move.x,move.y)
            if(pointerDown) {

                val distanceX=abs( origin.x-move.x +1)
                val distanceY=abs(origin.y -move.y+1)
                val dirx= (origin.x-move.x+1) / distanceX
                val diry=(origin.y-move.y +1 )/ distanceY
               // causes jagged effect whilst scrolling up  origin.set(move.x,move.y)
                listener.onSwipe()
                val frame=60f/FpsCounter.getInstance().getFps()
                offset.sub(dirx*threshHold*frame,threshHold*diry*frame)
                if(offset.x>=maxOffset.x&&maxOffset.x!=0f)
                    offset.x=offset.x+dirx*threshHold
                if(offset.x<=minOffset.x&&minOffset.x!=0f)
                    offset.x=offset.x+dirx*threshHold
                if(offset.y>=maxOffset.y&&maxOffset.y!=0f)
                    offset.y=offset.y+diry*threshHold*frame
                else if(offset.y<=minOffset.y&&minOffset.y!=0f)
                    offset.y=offset.y+diry*threshHold*frame

                UP=velocity.y<0
                DOWN=velocity.y>0
                LEFT=velocity.x<0
                RIGHT=velocity.x>0

            }


        }
        return false
    }

    interface OnSwipeListener{
       fun onSwipe()
    }

}