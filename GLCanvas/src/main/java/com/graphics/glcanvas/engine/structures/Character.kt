package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.maths.ColorRGBA

class Character(private var char:Char,private val font:Font): RectF(0f,0f,50f,50f) {

    private var outline=ColorRGBA()
    private var innerEdge=0f
    private var innerWidth=0f
    private var borderWidth=0f
    private var borderEdge=0f

     fun set(x:Float, y:Float,z:Float,width: Float,height: Float){
         val meta=font.getCharMetaData(char)
          set(x,y)
          setZ(z)
          setWidth(width)
          setHeight(height)
        getSpriteSheet().setSTMatrix(meta!!.getX(),meta.getY(),
            meta.getWidth(),meta.getHeight(),
            font.scaleW,font.scaleH,0)

    }

    fun getOutlineColor():ColorRGBA{
        return outline
    }

    fun setOutlineColor(outline:ColorRGBA){
        this.outline=outline
    }

    fun setOutlineColor(r:Float,g:Float,b:Float){
        this.outline.set(r,g,b)
    }

    fun setInnerEdge(innerEdge:Float){
        this.innerEdge=innerEdge
    }

    fun getInnerEdge():Float{
        return innerEdge
    }

    fun setInnerWidth(innerWidth:Float){
        this.innerWidth=innerWidth
    }

    fun getInnerWidth():Float{
        return innerWidth
    }

    fun setBorderWidth(borderWidth:Float){
        this.borderWidth=borderWidth
    }

    fun getBorderWidth():Float{
        return borderWidth
    }

    fun setBorderEdge(borderEdge:Float){
        this.borderEdge=borderEdge
    }

    fun getBorderEdge():Float{
        return borderEdge
    }
    fun getChar():Char{
        return char
    }

}