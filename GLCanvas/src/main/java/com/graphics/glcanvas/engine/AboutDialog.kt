package com.graphics.glcanvas.engine

import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Font
import com.graphics.glcanvas.engine.ui.*
import com.graphics.glcanvas.engine.utils.TextureAtlas

class AboutDialog(private val parent:GLView, atlas: TextureAtlas,font: Font, controller:GLCanvasSurfaceView.TouchController?, width:Float, height:Float) {
    private var layout=RelativeLayoutConstraint(null,width*0.8f, height*0.4f,atlas,"Card",0)
    private var scrollLayout=GLScrollLayout(width*0.8f, height*0.3f)
    private var inner= LinearLayoutConstraint(scrollLayout,width*0.8f, height*0.5f)
    private var title=GLLabel(150f,50f,font,"About",0.4f)
    private var description=GLLabel(inner.width*0.9f,height*0.8f,font,"Hello this is an openGL renderer created by charlie.\n\n You can contribute to the project at my github page @charlesgles9.\n\n I created this for fun and out of curiosity I wanted to demystify some UI concepts.\n\n I also wanted to create a simple but efficient renderer for android phones.\n\n I will keep on improving it and adding more cool features.",0.2f)
    private var close=GLImageButton(50f,50f,atlas)
    private var visible=false
    init {

        layout.addItem(scrollLayout)
        layout.addItem(close)
        layout.addItem(title)
        scrollLayout.setBackgroundColor(ColorRGBA.transparent)
        scrollLayout.addItem(inner)
        scrollLayout.setOrientation(GLScrollLayout.VERTICAL)
        layout.set(width*0.5f,height*0.5f)
        inner.addItem(description)
        layout.setZ(parent.getZ()+1f)
        inner.setBackgroundColor(ColorRGBA.transparent)
        description.setCenterText(false)
        description.getConstraints().layoutMarginTop(50f)
        description.getConstraints().layoutMarginLeft(10f)
        description.getConstraints().layoutMarginBottom(20f)
        scrollLayout.getConstraints().alignBelow(title)
        title.getConstraints().alignCenterHorizontal(layout)
        title.getConstraints().layoutMarginTop(20f)
        title.setTextColor(ColorRGBA.red)
        scrollLayout.addOnSwipeEvent(object :GLOnSwipeEvent.OnSwipeListener{
           override fun onSwipe() {

           }
       })
        close.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
                show(false)
               parent.setEnableTouchEvents(true)
            }
        })
        scrollLayout.setScrollBarProgressColor(ColorRGBA.red)
        controller?.getEvents()?.add(scrollLayout)
        controller?.getEvents()?.add(close)
        layout.setEnableTouchEvents(visible)
        close.setBackgroundImageAtlas("Checked",1)
        close.getConstraints().alignEnd(scrollLayout)
        close.getConstraints().layoutMarginRight(25f)
        scrollLayout.getConstraints().alignCenterVertical(layout)

    }

    fun draw(batch: Batch){
        if(visible)
        layout.draw(batch)
    }

    fun show(flag:Boolean){
        this.visible=flag
        layout.setEnableTouchEvents(visible)
    }

    fun isShowing():Boolean{
        return visible
    }
}