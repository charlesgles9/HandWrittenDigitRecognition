package com.graphics.glcanvas.engine.ui

abstract class Constraints {

    abstract fun toLeftOf(view:GLView)
    abstract fun toRightOf(view:GLView)
    abstract fun alignAbove(view:GLView)
    abstract fun alignBelow(view:GLView)
    abstract fun alignCenter(view:GLView)
    abstract fun alignEnd(view: GLView)
    abstract fun alignCenterVertical(view: GLView)
    abstract fun alignCenterHorizontal(view: GLView)
    abstract fun layoutMarginLeft(margin:Float)
    abstract fun layoutMarginRight(margin:Float)
    abstract fun layoutMarginTop(margin:Float)
    abstract fun layoutMarginBottom(margin:Float)
    abstract fun getMarginLeft():Float
    abstract fun getMarginRight():Float
    abstract fun getMarginTop():Float
    abstract fun getMarginBottom():Float
    abstract fun applyConstraints()


}