package com.graphics.glcanvas.engine.ui

open class GLLayoutParams (var width:Float, var height:Float){
   private var paddingLeft=0f
   private var paddingRight=0f
   private var paddingTop=0f
   private var paddingBottom=0f


    fun setPaddingLeft(paddingLeft:Float){
        this.paddingLeft=paddingLeft
    }

    fun setPaddingRight(paddingRight:Float){
        this.paddingRight=paddingRight
    }

    fun setPaddingTop(paddingTop:Float){
        this.paddingTop=paddingTop
    }

    fun setPaddingBottom(paddingBottom:Float){
        this.paddingBottom=paddingBottom
    }

    fun getPaddingLeft():Float{
        return paddingLeft
    }

    fun getPaddingRight():Float{
        return paddingLeft
    }

    fun getPaddingTop():Float{
        return paddingLeft
    }

    fun getPaddingBottom():Float{
        return paddingLeft
    }
}