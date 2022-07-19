package com.graphics.glcanvas.engine.ui

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.utils.TextureAtlas

open class RelativeLayoutConstraint(private val parent:GLView?,width:Float, height:Float):GLView(width ,height) {


    private var items= mutableListOf<GLView>()
    constructor(parent: GLView?,width:Float, height:Float, atlas: TextureAtlas, name:String,index:Int):this(parent,width, height){
       setBackgroundAtlas(atlas, name,index)
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

    fun setBackgroundAtlas(atlas: TextureAtlas, name:String,index: Int){
        this.atlas=atlas
        setBackgroundTextureAtlas(atlas)
        setPrimaryImage(name,index)
        setBackgroundSubTexture(name,index)
    }

    fun setBackgroundAtlas(atlas: TextureAtlas, name:String){
        this.atlas=atlas
        setBackgroundTextureAtlas(atlas)
        setPrimaryImage(name,0)
        setBackgroundSubTexture(name,0)
    }
    private fun applyMargin(view:GLView){
        view.set(view.getX()+view.getConstraints().getMarginLeft(),view.getY())
        view.set(view.getX()-view.getConstraints().getMarginRight(),view.getY())
        view.set(view.getX(),view.getY()+view.getConstraints().getMarginTop())
        view.set(view.getX(),view.getY()-view.getConstraints().getMarginBottom())
    }

    private fun removeMargin(view:GLView){
        view.set(view.getX()-view.getConstraints().getMarginLeft(),view.getY())
        view.set(view.getX()+getConstraints().getMarginRight(),view.getY())
        view.set(view.getX(),view.getY()-view.getConstraints().getMarginTop())
        view.set(view.getX(),view.getY()+view.getConstraints().getMarginBottom())
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
    override fun setVisibility(visible: Boolean) {
        super.setVisibility(visible)
        for(view in items){
            view.setVisibility(visible)
        }
    }
    override fun setZ(z: Float) {
        super.setZ(z)
        items.forEach{
            it.setZ(z)
        }
    }
    override fun draw(batch: Batch) {
        super.draw(batch)

        items.forEach {
            it.set(getX()-width*0.5f+it.width*0.5f,getY()-height*0.5f+it.height*0.5f)
            applyMargin(it)
        }
        if(isVisible())
        items.forEach {
            LayoutConstraint.clipView(parent?:this,this,it)
            it.draw(batch)
        }
        items.forEach {
            removeMargin(it)
        }

    }
}