package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.utils.TextureAtlas

class GLGridLayout(private val parent:GLView?,width:Float,height:Float,private val rows:Int,private val cols:Int):GLView(width ,height) {
    private var items= mutableListOf<GLView>()
    private var offset= Vector2f()
    private var onItemClick=OnItemClickEvent(null,parent?:this,null,items)
    private var listener:OnItemClickEvent.OnItemClickListener?=null
    constructor(parent:GLView?,width:Float,height:Float, rows:Int,cols:Int,atlas: TextureAtlas,name:String,index:Int):this(parent,width, height,rows, cols){
        this.atlas=atlas
        setBackgroundImageAtlas(atlas, name,index)
    }

    fun setBackgroundImageAtlas(atlas: TextureAtlas, name:String, index:Int){
        setBackgroundTextureAtlas(atlas)
        setPrimaryImage(name,index)
        setBackgroundSubTexture(name,index)
    }
    fun setBackgroundImageAtlas(atlas: TextureAtlas, name:String){
        setBackgroundTextureAtlas(atlas)
        setPrimaryImage(name,0)
        setBackgroundSubTexture(name,0)
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

    fun getItems():MutableList<GLView>{
        return items
    }
    override fun setEnableTouchEvents(enable: Boolean){
        super.setEnableTouchEvents(enable)
        items.forEach {
            it.setEnableTouchEvents(enable)
        }
    }
    override fun setEnabled(enable: Boolean) {
        super.setEnabled(enable)
        items.forEach {
            it.setEnabled(enable)
        }
    }
    fun setOnItemClickListener(listener:OnItemClickEvent.OnItemClickListener){
        this.listener=listener
        this.onItemClick.setListener(listener)
    }
    override fun draw(batch: Batch) {
        super.draw(batch)
        LayoutConstraint.groupItems(offset,this,items, rows, cols)
        items.forEach {
            LayoutConstraint.clipView(parent?:this,this,it)
            it.draw(batch)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(isTouchEventsEnabled())
        onItemClick.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}