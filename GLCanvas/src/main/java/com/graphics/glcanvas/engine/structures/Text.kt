package com.graphics.glcanvas.engine.structures

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.maths.Vector3f
import com.graphics.glcanvas.engine.utils.TextureLoader
import java.lang.IllegalStateException
import kotlin.concurrent.thread
import kotlin.math.max

class Text(private var text:String,private var fontSize:Float,private var font: Font) {
    private val words=ArrayList<Word>()
    private val color=ColorRGBA()
    val position=Vector3f()
    private var outline=ColorRGBA()
    private var clipUpper=Vector2f(Float.MIN_VALUE, Float.MIN_VALUE)
    private var clipLower=Vector2f(Float.MAX_VALUE, Float.MAX_VALUE)
    private var innerEdge=0f
    private var innerWidth=0f
    private var borderWidth=0f
    private var borderEdge=0f
    private var maxWidth=Float.MAX_VALUE
    private var maxHeight=Float.MAX_VALUE
    private var visible=true
    private var paragraphs= mutableListOf<MutableList<String>>()
    private var overallWidth=0f
    var width=0f
    var height=0f
    init {
        initParagraph()
        initWordList()

    }

    private fun initParagraph(){
        paragraphs.clear()
        val pText=text.split("\n")
        // paragraph list in text
        for( p in pText){
            // words list in text
            paragraphs.add(p.split(" ").toMutableList())
        }
    }

    private fun initWordList(){
       overallWidth=0f
        words.clear()
        val cursor=Vector2f()
        if(paragraphs.isNotEmpty()) {
            for(p in paragraphs) {
                for (w in p)
                    addWord(w, cursor)
                width= max(cursor.x,width)
                height=max(cursor.y,height)
                //move to next line
                cursor.x=0f
                cursor.y+=font.lineHeight*fontSize

            }
        }
    }




    private fun addWord(text: String, cursor:Vector2f){
        // word spacing
        val space=20f
            words.add(Word(text,font,cursor,fontSize,clipUpper,clipLower,color,outline,innerEdge, innerWidth,
                                           borderWidth, borderEdge, position, maxWidth,maxHeight))
            cursor.addX(space*fontSize)
          words.last().getCharacter().forEach {
              overallWidth+=it.getWidth()+space*fontSize
          }


    }

    fun set(x:Float,y:Float,z:Float){
        // update only if necessary
        if(x!=position.x||position.y!=y||position.z!=z) {
            this.position.set(x, y,z)

            initWordList()

        }
    }




    fun setMaxWidth(maxWidth:Float){
        this.maxWidth=maxWidth
        initWordList()
    }

    fun setMaxHeight(maxHeight:Float){
        this.maxHeight=maxHeight
    }

    fun setFontSize(fontSize: Float){
        this.fontSize=fontSize
        initWordList()
    }

    fun setColor(color: ColorRGBA){
        this.color.set(color)
        words.forEach { word->
            word.getCharacter().forEach {
              it.setColor(color)
            }
        }
    }

    fun setText(text: String){
        if(text!=this.text) {
            this.text = text
            initParagraph()
            initWordList()
        }
    }

    fun setClipUpper(x:Float,y:Float) {
        clipUpper.set(x,y)

    }

    fun setClipLower(x:Float,y:Float) {
        clipLower.set(x,y)
    }

    fun getText():String{
        return text
    }

    fun getOverallWidth():Float{
        return overallWidth
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

    fun setInnerWidth(innerWidth:Float){
        this.innerWidth=innerWidth
    }

    fun setBorderWidth(borderWidth:Float){
        this.borderWidth=borderWidth
    }

    fun setBorderEdge(borderEdge:Float){
        this.borderEdge=borderEdge
    }

    fun setVisibility(visible:Boolean){
        this.visible=visible
    }

    fun isVisible():Boolean{
        return visible
    }
    fun draw(batch: Batch){
        val id=TextureLoader.getInstance().getTexture(font.getTextureAtlasPath())
        try {
            if (visible)
                synchronized(words) {
                    for (i in 0 until words.size) {
                        val word = words[i]
                        for (j in 0 until word.getCharacter().size) {
                            val char = word.getCharacter()[j]
                            char.getTexture().setId(id)
                            batch.draw(char)
                        }
                    }
                }
        }catch (ignore:IllegalStateException){ }


    }

}