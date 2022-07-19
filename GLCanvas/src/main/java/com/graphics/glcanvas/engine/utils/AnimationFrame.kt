package com.graphics.glcanvas.engine.utils

class AnimationFrame(//represents how long this animation will last in millis
                    private var frameLength: Array<Long>,
                     //frames to be displayed
                     private var frames: Array<Int>) {

    private var tick=0

    constructor(time:Long,width:Int,height:Int):
            this(Array(width*height,init = {time}),Array<Int>(width*height) { i ->  (i)})  {

    }
    fun setFrameLength(frameLength: Array<Long>){
        this.frameLength=frameLength
    }

    fun setFrames(frames: Array<Int>){
        this.frames=frames
    }

    fun getFrameLength():Array<Long>{
        return frameLength
    }

    fun getFrames():Array<Int>{
        return frames
    }

    fun getTick():Int{
        return tick
    }

    fun increment(){
        tick++
    }

    fun getCurrent():Int{
        return frames[tick]
    }

    fun reset(){
        tick=0
    }

}