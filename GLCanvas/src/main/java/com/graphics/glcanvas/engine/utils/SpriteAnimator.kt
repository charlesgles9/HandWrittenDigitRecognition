package com.graphics.glcanvas.engine.utils

class SpriteAnimator(name: String, frame: AnimationFrame, sheet: SpriteSheet) {

    private var lastFrameTime=0L
    private var activated=false
    private var looping=false
    private var current:String?=null

    private class Container(private var frame: AnimationFrame, private var sheet: SpriteSheet) {
        fun getFrame():AnimationFrame{
            return frame
        }
        fun getSheet():SpriteSheet{
            return sheet
        }
    }

    private val frames=HashMap<String,Container>()

    init {
        put(name, frame, sheet)
        setCurrent(name)
    }

    fun put(name: String,frame: AnimationFrame,sheet: SpriteSheet){
        frames[name] = Container(frame,sheet)

    }

    fun setCurrent(current: String){
        this.current=current
    }

    fun getCurrentFrame():AnimationFrame?{
        return frames[current]?.getFrame()
    }

    fun getCurrentSpriteSheet():SpriteSheet?{
        return frames[current]?.getSheet()
    }
    fun isActivated():Boolean{
        return activated
    }

    fun setActivated(activated:Boolean){
        this.activated=activated
    }

    fun isLooping():Boolean{
        return looping
    }

    fun setLooping(looping:Boolean){
        this.looping=looping
    }

    fun reset(){
        getCurrentFrame()?.reset()
    }

    fun update(time:Long){
        if(activated){
            val frame=getCurrentFrame()
            if(frame!=null)
            if(time>lastFrameTime+frame.getFrameLength()[frame.getTick()]){
                lastFrameTime=time
                frame.increment()
                if(frame.getTick()>frame.getFrameLength().size-1){
                    reset()
                    activated=isLooping()
                }
                getCurrentSpriteSheet()?.setCurrentFrame(frame.getTick())
            }
        }
    }
}