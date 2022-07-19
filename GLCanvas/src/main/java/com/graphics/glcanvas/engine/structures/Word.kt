package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.maths.Vector3f


class Word(str:String, font: Font, cursor:Vector2f, size:Float, clipUpper:Vector2f, clipLower:Vector2f, color: ColorRGBA, outline:ColorRGBA,
           innerEdge:Float, innerWidth:Float, borderWidth:Float, borderEdge:Float, position: Vector3f, maxWidth:Float, maxHeight:Float) {
    private val characters=ArrayList<Character>()
    init {
        // test if this whole word can fit in this line
        var testWidth=0f
        for(i in str.indices) {
            val meta = font.getCharMetaData(str[i])
            //how further this character is spaced to the next
            val advance = meta!!.getAdvanceX() * size
            testWidth+=advance-font.padding[Font.PADDING_LEFT]
        }
        //move to next line if the virtual cursor has reached maximum width
        if((cursor.x+testWidth)>=maxWidth){
            cursor.x=0f
            cursor.y+=font.lineHeight*size
        }

        if(cursor.y<maxHeight-font.lineHeight*size)
        for(i in str.indices){
            val char=Character(str[i],font)
            val meta=font.getCharMetaData(str[i])
            //how further this character is spaced to the next
            val advance=meta!!.getAdvanceX()* size
            //character width and height
            val fontSizeX=meta.getWidth()* size
            val fontSizeY=meta.getHeight()* size
            char.setColor(color)
            char.set(
                /* lets offset of x and y by advance/2 since our origin
                   is at the center of each quad. Took me hours to figure this shit out
                 */
                position.x+cursor.x+advance*0.5f+(meta.getOffsetX()*size)*0.5f,position.y+cursor.y+(meta.getOffsetY()* size)*0.5f,position.z,
                fontSizeX,
                fontSizeY )
                characters.add(char)
            char.setOutlineColor(outline)
            char.setInnerEdge(innerEdge)
            char.setInnerWidth(innerWidth)
            char.setBorderEdge(borderEdge)
            char.setBorderWidth(borderWidth)
            char.setClipUpper(clipUpper.x,clipUpper.y)
            char.setClipLower(clipLower.x,clipLower.y)
            // subtract the padding for proper char spacing
            cursor.addX(advance+font.padding[Font.PADDING_LEFT]*0.5f)

           //@debug println("char = ${char.getChar()} w $fontSizeX h $fontSizeY advX ${meta!!.getAdvanceX()} OffsetX ${meta.getOffsetX()} OffsetY ${meta.getOffsetY()}")
        }
    }


    fun updateWord(font: Font, cursor:Vector2f, size:Float, clipUpper:Vector2f,clipLower:Vector2f, color: ColorRGBA, outline:ColorRGBA,
                   innerEdge:Float, innerWidth:Float, borderWidth:Float, borderEdge:Float, position:Vector3f){

            for(char in characters){
                val meta=font.getCharMetaData(char.getChar())
                //how further this character is spaced to the next
                val advance=meta!!.getAdvanceX()* size
                //character width and height
                val fontSizeX=meta.getWidth()* size
                val fontSizeY=meta.getHeight()* size
                char.setColor(color)
                char.set(
                    /* lets offset of x and y by advance/2 since our origin
                       is at the center of each quad. Took me hours to figure this shit out
                     */
                    position.x+cursor.x+advance*0.5f+(meta.getOffsetX()*size)*0.5f,position.y+cursor.y+(meta.getOffsetY()* size)*0.5f,position.z,
                    fontSizeX,
                    fontSizeY )
                char.setOutlineColor(outline)
                char.setInnerEdge(innerEdge)
                char.setInnerWidth(innerWidth)
                char.setBorderEdge(borderEdge)
                char.setBorderWidth(borderWidth)
                char.setClipUpper(clipUpper.x,clipUpper.y)
                char.setClipLower(clipLower.x,clipLower.y)
                // subtract the padding for proper char spacing
                cursor.addX(advance+font.padding[Font.PADDING_LEFT]*0.5f)

                //@debug println("char = ${char.getChar()} w $fontSizeX h $fontSizeY advX ${meta!!.getAdvanceX()} OffsetX ${meta.getOffsetX()} OffsetY ${meta.getOffsetY()}")
            }
    }

    fun getCharacter():ArrayList<Character>{
        return characters
    }
}
