package com.graphics.glcanvas.engine.ui

import com.graphics.glcanvas.engine.maths.ColorRGBA

class GLCheckBox(width:Float,height:Float,color: ColorRGBA):GLView(width, height) {


    init {
        getBackground().setThickness(width*0.1f)
        getForeground().setWidth(width*0.9f)
        getForeground().setHeight(height*0.9f)
        getBackground().setColor(color)
       isCheckBox=true
    }


    fun setCheckedColor(color: ColorRGBA){
        setRippleColor(color)
    }






}