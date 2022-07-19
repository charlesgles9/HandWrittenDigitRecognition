package com.graphics.glcanvas.engine.structures

class FontMetaData(private val char: Char,private val x:Float,private val y:Float,
                   private val width:Float,private val height:Float,
                   private val xOffset:Float,private val yOffset:Float,private val xAdvance:Float) {
    fun getChar():Char{
        return char
    }
    fun getX():Float{
        return x
    }
    fun getY():Float{
        return y
    }
    fun getWidth():Float{
        return width
    }
    fun getHeight():Float{
        return height
    }
    fun getOffsetX():Float{
        return xOffset
    }
    fun getOffsetY():Float{
        return yOffset
    }
    fun getAdvanceX():Float{
        return xAdvance
    }

}