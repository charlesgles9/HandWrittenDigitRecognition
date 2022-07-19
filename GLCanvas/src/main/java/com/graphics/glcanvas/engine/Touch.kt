package com.graphics.glcanvas.engine

import android.view.MotionEvent

interface Touch {

    fun onTouchEvent(event: MotionEvent):Boolean

}