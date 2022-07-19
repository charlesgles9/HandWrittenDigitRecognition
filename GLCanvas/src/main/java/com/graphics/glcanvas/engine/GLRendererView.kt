package com.graphics.glcanvas.engine


open class GLRendererView(private val width: Float, private val height: Float) :Updatable {


    private val renderer = GLRenderer(this)

    override fun draw() {


    }

    override fun update(delta: Long) {

    }

    override fun prepare() {


    }

    fun getRenderer(): GLRenderer {
        return renderer
    }

    fun getController():GLCanvasSurfaceView.TouchController?{
        return renderer.getTouchController()
    }

    fun getCanvasWidth():Float{
       return width
    }

    fun getCanvasHeight():Float{
        return height
    }
}