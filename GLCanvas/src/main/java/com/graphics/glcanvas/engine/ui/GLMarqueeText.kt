package com.graphics.glcanvas.engine.ui

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Font
import com.graphics.glcanvas.engine.utils.FpsCounter

class GLMarqueeText(width:Float, height:Float,text:String,font: Font,size:Float):RelativeLayoutConstraint(null, width, height) {

    private val label=GLLabel(width,height,font,text,size)
    private var direction= MOVE_RIGHT
    private var velocity=1.2f
    private var textWidth=0f
    private var textHeight=0f
    private val offset=Vector2f()
    companion object{
        const val MOVE_LEFT=0
        const val MOVE_RIGHT=1
    }
    init {
        val lw=label.getTextView()?.getOverallWidth()?:width
        textHeight=label.getTextView()?.height?:height
        textWidth=lw
        label.setBackgroundColor(ColorRGBA.transparent)
        label.setWidthPixels(textWidth)
        label.getTextView()?.setMaxWidth(lw)
    }

   fun setText(text:String){
       label.setText(text)
   }

   fun setTextColor(color:ColorRGBA){
       label.setTextColor(color)
   }

   fun setDirection(direction:Int){
        this.direction=direction
    }

    fun setVelocity(velocity:Float){
        this.velocity=velocity
    }

    override fun draw(batch: Batch) {
        super.draw(batch)
        LayoutConstraint.clipView(this,label)
        label.draw(batch)
        when(direction){
            MOVE_LEFT->{
                label.set(getX()+textWidth*0.5f+width*0.5f,getY()+textHeight*0.5f)
                offset.x-=velocity*60f/FpsCounter.getInstance().getFps()
                label.setX(label.getX()+offset.x)
                if((label.getX()+textWidth*0.5f)<=getX()-width*0.5f){
                    offset.x=0f
                }
            }
            MOVE_RIGHT->{
                label.set(getX()-textWidth*0.5f-width*0.5f,getY()+textHeight*0.5f)
                offset.x+=velocity*60f/FpsCounter.getInstance().getFps()
                label.setX(label.getX()+offset.x)
                if((label.getX()-textWidth*0.5f)>=getX()+width*0.5f){
                    offset.x=0f
                }
            }
        }



    }
}