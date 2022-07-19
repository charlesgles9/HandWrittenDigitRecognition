package com.graphics.glcanvas.engine.utils
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.structures.Text

class FpsCounter() {


    companion object{
        private val instance=FpsCounter()
        private var fps=60
        private var pt=0L
        private var counter=0L
        private var delay=1000L
        private var text:Text?=null
        fun getInstance():FpsCounter{
            return instance
        }
        fun setGUITextView(view: Text){
           this.text=view
        }
    }

    fun update(time:Long){
        val delta=pt+ delay
        if(delta<=time){
            fps+= counter.toInt()
            fps=(fps*0.5f).toInt()+1
            text?.setText("FPS: $fps")
            pt=time
            counter=0
        }else {
            counter++
        }

    }


    fun draw(batch: Batch){
        text?.draw(batch)
    }

    fun getFps():Int{
        return fps
    }








}

