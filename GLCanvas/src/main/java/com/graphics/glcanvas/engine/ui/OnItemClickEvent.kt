package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Touch
import com.graphics.glcanvas.engine.maths.Vector2f

class OnItemClickEvent(private var listener:OnItemClickListener?,
                       private val bounds:GLView,
                       private val header:GLView?,
                       private val items:MutableList<GLView>): Touch {

    private var pointerDown=BooleanArray(1)
    private var position= Vector2f(-1f,-1f)
    fun contains(view:GLView,x:Float,y:Float):Boolean{
        return view.contains(x,y)
    }

  fun setListener(onItemClickListener: OnItemClickListener){
      this.listener=onItemClickListener
  }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(items.size!=pointerDown.size)
            pointerDown= BooleanArray(items.size)
            for (i in 0 until  items.size) {
                val view = items[i]
                if (event.action == MotionEvent.ACTION_DOWN) {
                    position.set(
                        event.x - view.getThumbSize() / 2,
                        event.y - view.getThumbSize() / 2
                    )
                    ScreenRatio.getInstance().project(position)
                    //make sure the view isn't close to the header in the dropdown
                    pointerDown[i] = contains(view, position.x, position.y)&&bounds.contains(view)&&!(header?.contains(view)?:false)

                } else if (event.action == MotionEvent.ACTION_UP && pointerDown[i]) {
                    position.set(-1f, -1f)
                    listener?.onItemClick(view)
                    header?.getTextView()?.setText(view.getTextView()?.getText() ?: "")
                    pointerDown[i] = false
                     if(header is GLDropDown)
                         header.toggleDropDown(false)
                } else if (event.action == MotionEvent.ACTION_MOVE && pointerDown[i]) {
                    position.set(
                        event.x - view.getThumbSize() / 2,
                        event.y - view.getThumbSize() / 2
                    )
                    ScreenRatio.getInstance().project(position)
                    pointerDown[i] = false

                }
            }

        return true
    }
    interface OnItemClickListener{
       fun onItemClick(view:GLView)
    }
}