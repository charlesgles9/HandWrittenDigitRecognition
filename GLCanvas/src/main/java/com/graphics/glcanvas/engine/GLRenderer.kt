package com.graphics.glcanvas.engine
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.os.SystemClock
import com.graphics.glcanvas.engine.utils.FpsCounter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(private val updatable: Updatable) : GLSurfaceView.Renderer {

    private var controller:GLCanvasSurfaceView.TouchController?=null
    //start time
    private var st=SystemClock.elapsedRealtimeNanos()/1000000L
    //fps limit default at 60 to prevent vsync
    private var cap=60
    private var vsync=true
    override fun onDrawFrame(gl: GL10?) {
        //expected cycle
       // val  ms_per_frame=if(!vsync)1000L/cap else 0L
        val time= SystemClock.elapsedRealtimeNanos()/1000000L
      //  val elapsed=time-st
    //    if(elapsed>=ms_per_frame){
            updatable.draw( )
            updatable.update(time)
            FpsCounter.getInstance().update(time)
        //    st+=ms_per_frame
     //   }
        /* val nextCycle=st+(1000L/60.0f).toLong()
         if(time<nextCycle){
             Thread.sleep(nextCycle-time)
         }
        st=time*/
    }

    fun fpsCap(cap:Int){
        this.cap=cap
    }

    fun enableVSync(enable:Boolean){
        this.vsync=enable
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        GLES32.glViewport(0,0,width, height)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL)
        GLES32.glDepthMask(true)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        updatable.prepare()
        setTransparency(gl!!,true)
    }


    fun setController(controller:GLCanvasSurfaceView.TouchController){
       this.controller=controller
    }

    fun getTouchController():GLCanvasSurfaceView.TouchController?{
        return controller
    }

     private fun setTransparency(gl:GL10, transparency:Boolean){
        if(transparency){
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_ONE,GL10.GL_ONE_MINUS_SRC_ALPHA)
        }else{
            gl.glEnable(GL10.GL_BLEND)
        }
    }
}