package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.RectF
import com.graphics.glcanvas.engine.utils.FpsCounter
import com.graphics.glcanvas.engine.utils.TextureAtlas
import kotlin.math.max
import kotlin.math.min

class GLScrollLayout(width:Float,height:Float):GLView(width,height) {

    private var items= mutableListOf<GLView>()
    private var orientation= VERTICAL
    private var offset=Vector2f()
    private var onSwipeEvent:GLOnSwipeEvent?=null
    private var onSwipeListener:GLOnSwipeEvent.OnSwipeListener?=null
    private val scrollBarProgress=RectF()
    private val scrollBarBackground=RectF()
    private var scrollBarHeight=10f
    private var scrollBarWidth=10f
    private var enableScrollBar=true

    companion object{
        const val VERTICAL=0
        const val HORIZONTAL=1
    }
    constructor(width:Float, height:Float, atlas: TextureAtlas, name:String,index: Int):this(width, height){
        setBackgroundAtlas(atlas, name,index)
    }

     fun setBackgroundAtlas(atlas: TextureAtlas, name:String,index:Int){
         this.atlas=atlas
         setBackgroundTextureAtlas(atlas)
         setPrimaryImage(name,index)
         setBackgroundSubTexture(name,index)
     }

    fun showScrollBar(enableScrollBar:Boolean){
        this.enableScrollBar=enableScrollBar
    }

    fun setScrollBarProgressColor(color: ColorRGBA){
        scrollBarProgress.setColor(color)
    }

    fun setScrollBarBackgroundColor(color: ColorRGBA){
        scrollBarBackground.setColor(color)
    }

    fun setScrollBarProgressFromAtlas(atlas: TextureAtlas,name: String,index: Int){
       scrollBarProgress.setSubTextureAtlas(atlas, name, index)
    }

    fun setScrollBarBackgroundFromAtlas(atlas: TextureAtlas,name: String,index: Int){
        scrollBarBackground.setSubTextureAtlas(atlas, name,index)
    }
    fun setScrollBarProgressFromAtlas(atlas: TextureAtlas,name: String){
        scrollBarProgress.setSubTextureAtlas(atlas, name, 0)
    }

    fun setScrollBarBackgroundFromAtlas(atlas: TextureAtlas,name: String){
        scrollBarBackground.setSubTextureAtlas(atlas, name,0)
    }
    fun setScrollBarHeight(height: Float){
        scrollBarHeight=height
    }

    //push this view from center origin 0.5,0.5 -> 0,0
    fun setPosition(x:Float,y:Float){
        set(x+width*0.5f,y+height*0.5f)
    }

    fun setColor(color: ColorRGBA){
        setBackgroundColor(color)
    }

    fun setItems(items:MutableList<GLView>){
        this.items.addAll(items)

    }

    fun addItem(view:GLView){
        this.items.add(view)
    }

    override fun setVisibility(visible: Boolean) {
        super.setVisibility(visible)
        for(view in items){
            view.setVisibility(visible)
        }
    }

    override fun setEnableTouchEvents(enable: Boolean){
         super.setEnableTouchEvents(enable)
        items.forEach {
            it.setEnableTouchEvents(enable)
        }
    }

    override fun setZ(z: Float) {
        super.setZ(z)
        scrollBarProgress.setZ(z)
        scrollBarBackground.setZ(z)
        items.forEach{
            it.setZ(z)
        }
    }
    fun setOrientation(orientation:Int){
        this.orientation=orientation
    }

    fun addOnSwipeEvent(onSwipeListener:GLOnSwipeEvent.OnSwipeListener){
        this.onSwipeEvent= GLOnSwipeEvent(onSwipeListener,this)
        this.onSwipeListener=onSwipeListener
        onSwipeEvent?.setOffset(offset)
    }


    private fun scrollVertical(oHeight: Float){
       val rollBackVelocity=2.5f*60f/(FpsCounter.getInstance().getFps()+1f)
        onSwipeEvent?.setMaxOffset(0f,height*0.20f)
        onSwipeEvent?.setMinOffset(0f,-oHeight+height*0.8f)
        //roll back effect
       if(onSwipeEvent?.getPointerDown() != true) {
           if (offset.y >= 10f)
               offset.sub(0f, rollBackVelocity)
           if (offset.y < (oHeight - height) * -1f)
               offset.add(0f, rollBackVelocity)
       }

    }

    private fun scrollHorizontal(oWidth: Float){
        val rollBackVelocity=2.5f*60f/(FpsCounter.getInstance().getFps()+1f)
        onSwipeEvent?.setMaxOffset(width*0.20f,0f)
        onSwipeEvent?.setMinOffset(-oWidth+width*0.8f,0f)
        if(onSwipeEvent?.getPointerDown() != true) {
            if (offset.x >= 10f)
                offset.sub(rollBackVelocity, 0f)
            if (offset.x < (oWidth - width) * -1f)
                offset.add(rollBackVelocity, 0f)
        }
    }


    private fun drawHorizontalProgress(batch: Batch,oHeight: Float){
        val minHeight=oHeight-height
        val farHeight= max(offset.y*-1f,0f)
        // were in 2D space is this progress bar located relative to the last scrolling position
        var farPercent=(farHeight/minHeight)
        // how large is the scroll progress bar relative to the background progress bar
        var scrollPercent=height/oHeight
        if(scrollPercent>1.0f)
            scrollPercent=1.0f
        if(farPercent>1.0f)
            farPercent=1.0f

        scrollBarBackground.setWidth(scrollBarWidth)
        scrollBarProgress.setWidth(scrollBarWidth)
        scrollBarBackground.setHeight(height*0.8f)
        scrollBarProgress.setHeight(max(scrollBarBackground.getHeight()*scrollPercent-scrollBarBackground.getHeight()*(1f-scrollPercent),25f))
        scrollBarBackground.set(getX()+width*0.5f-scrollBarWidth,getY())

        if(scrollBarBackground.getHeight()!=scrollBarProgress.getHeight()) {
            val sH = scrollBarBackground.getHeight()
            val offset = (sH) * (farPercent)
            var py =
                scrollBarBackground.getY() + offset - (sH - scrollBarProgress.getHeight()) * 0.5f
            py =
                min(scrollBarBackground.getY() + (scrollBarBackground.getHeight() - scrollBarProgress.getHeight()) * 0.5f,
                    py)
            scrollBarProgress.set(scrollBarBackground.getX(), py)
        }else{
            scrollBarProgress.set(scrollBarBackground.getX(),scrollBarBackground.getY())
        }
        batch.draw(scrollBarBackground)
        batch.draw(scrollBarProgress)

    }

    private fun drawVerticalProgress(batch: Batch,oWidth: Float){
        val minWidth=oWidth-width
        // how large is the scroll progress bar relative to the background progress bar
        var scrollPercent=width/oWidth
        val farHeight=min(minWidth,max(offset.x*-1f,1f))
        // were in 2D space is this progress bar located relative to the last scrolling position
        var farPercent=((farHeight+1f)/minWidth)
        if(scrollPercent>1.0f)
            scrollPercent=1.0f
        if(farPercent>1.0f)
            farPercent=1.0f

        scrollBarBackground.setWidth(width*0.8f)
        scrollBarProgress.setWidth(max(scrollBarBackground.getWidth()*scrollPercent-scrollBarBackground.getWidth()*(1f-scrollPercent),25f))
        scrollBarBackground.setHeight(scrollBarHeight)
        scrollBarProgress.setHeight(scrollBarHeight*0.8f)
        scrollBarBackground.set(getX(),getY()+height*0.5f-scrollBarHeight)
        val sW=scrollBarBackground.getWidth()
        val offset=sW*(farPercent)
        var px=scrollBarBackground.getX()+offset-(sW-scrollBarProgress.getWidth())*0.5f
            px= min(px,(scrollBarBackground.getWidth()-scrollBarProgress.getWidth())*0.5f+scrollBarBackground.getX())
        scrollBarProgress.set(px,scrollBarBackground.getY())
        batch.draw(scrollBarBackground)
        batch.draw(scrollBarProgress)

    }
    private fun drawScrollBar(batch: Batch,itemWidth:Float,itemHeight:Float){
        val last=items.last()
         if(orientation== HORIZONTAL)
             drawVerticalProgress(batch,itemWidth)
            else
             drawHorizontalProgress(batch,itemHeight)
    }

    override fun draw(batch: Batch) {
        super.draw(batch)
        LayoutConstraint.groupItems(orientation,offset ,this,items)
        var itemWidth=0f
        var itemHeight=0f
        if(isVisible()) {
            items.forEach {
                LayoutConstraint.clipView(this, it)
                itemWidth += it.width
                itemHeight += it.height
                it.draw(batch)
            }
            if (enableScrollBar)
                drawScrollBar(batch, itemWidth, itemHeight)
        }
        when(orientation){
            VERTICAL->
                scrollVertical(itemHeight)
            HORIZONTAL->
                scrollHorizontal(itemWidth)
        }

    }

    override fun onTouchEvent(event: MotionEvent):Boolean {
        super.onTouchEvent(event)
        if(isTouchEventsEnabled()) {
            if (isEnabled() && isVisible())
                onSwipeEvent?.onTouchEvent(event)!!
            items.forEach {
                it.onTouchEvent(event)
            }
        }
        return true
    }
}