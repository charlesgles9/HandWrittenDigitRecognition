package com.graphics.glcanvas.engine

import android.annotation.SuppressLint
import android.app.Activity
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.graphics.glcanvas.engine.ui.ScreenRatio


@SuppressLint("ViewConstructor")
class GLCanvasSurfaceView(private val context: Activity, private val renderer: GLRendererView) : GLSurfaceView(context) {

    private val controller=TouchController()
    init {
        hideStatusBar()
        setEGLContextClientVersion(3)
        setScreenDisplayRatio()
        holder.setFixedSize(renderer.getCanvasWidth().toInt(), renderer.getCanvasHeight().toInt())
        ScreenRatio.getInstance().setSurfaceScreen(renderer.getCanvasWidth(),renderer.getCanvasHeight())
        renderer.getRenderer().setController(controller)
        setRenderer(renderer.getRenderer())

    }


     private fun hideStatusBar(){
         context.requestWindowFeature(Window.FEATURE_NO_TITLE)
         context.window.setFlags(
             WindowManager.LayoutParams.FLAG_FULLSCREEN,
             WindowManager.LayoutParams.FLAG_FULLSCREEN
         )
        val insetsController=ViewCompat.getWindowInsetsController(context.window.decorView)?:return
        insetsController.systemBarsBehavior=
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

    }

    // in case there is status bar or system bar on top
    private fun setScreenDisplayRatio(){
        val content=context.window.findViewById<View>(Window.ID_ANDROID_CONTENT)
        val w=resources.displayMetrics.widthPixels- (resources.displayMetrics.widthPixels-content.width)
        val h=resources.displayMetrics.heightPixels- (resources.displayMetrics.heightPixels- content.height)
        ScreenRatio.getInstance().setDisplayScreen(w.toFloat(),h.toFloat())
    }
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
       setScreenDisplayRatio()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        queueEvent {
            for(touch in controller.getEvents()){
                touch.onTouchEvent(event)

            }
        }

        return true
    }


    override fun onPause() {
        super.onPause()
        renderMode= RENDERMODE_WHEN_DIRTY
    }

    override fun onResume() {
        super.onResume()
        renderMode= RENDERMODE_CONTINUOUSLY
    }

 inner class TouchController{
     private val events= mutableListOf<Touch>()
     fun addEvent(touch: Touch){
         events.add(touch)
     }
     fun getEvents():MutableList<Touch>{
         return events
     }
 }

}