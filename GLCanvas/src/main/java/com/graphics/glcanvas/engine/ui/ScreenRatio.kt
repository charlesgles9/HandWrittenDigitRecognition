package com.graphics.glcanvas.engine.ui

import com.graphics.glcanvas.engine.maths.Vector2f

class ScreenRatio {

    companion object{
        private val instance=ScreenRatio()
        private val screen= Vector2f()
        private val display= Vector2f()


        fun getInstance():ScreenRatio{
            return instance
        }
    }

    fun project(vector:Vector2f):Vector2f{
        val ratioX=(screen.x+1f)/(display.x+1f)
        val ratioY=(screen.y+1f)/(display.y+1f)
        vector.set(vector.x*ratioX,vector.y*ratioY)
        return vector
    }


    fun  setSurfaceScreen(x:Float,y:Float){
        screen.set(x,y)
    }

    fun setDisplayScreen(x:Float,y:Float){
        display.set(x,y)
    }

    fun getDisplay():Vector2f{
        return display
    }

    fun getSurfaceScreen():Vector2f{
        return screen
    }
}