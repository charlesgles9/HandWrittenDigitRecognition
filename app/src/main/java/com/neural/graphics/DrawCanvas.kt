package com.neural.graphics
import android.graphics.Color
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.AxisABB
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.RectF
import com.graphics.glcanvas.engine.ui.MultiTouchEvent
import com.neural.graphics.mnistReader.MnistMatrix


class DrawCanvas(private val position: Vector2f,private val pixelSize:Float):Update{

    private var event:MultiTouchEvent?=null
    private var entity=MnistEntity(MnistMatrix(28,28),position,pixelSize)
    private val axis=AxisABB()
    init {
        val touchRect=RectF(0f,0f,16f,16f)
        entity.initGrid(MnistMatrix(28,28))
        event=MultiTouchEvent(object :MultiTouchEvent.OnMultiTouchListener{
            override fun onTouch(vector2f: Vector2f) {

               for(i in 0 until entity.getQuads().size){
                   val rect=entity.getQuads()[i]
                   touchRect.set(vector2f.x,vector2f.y)
                   if(collides(rect,touchRect))
                       entity.setColor(i, ColorRGBA.white,1.0f)
               }
            }
            override fun onRelease() {

            }
          }, entity.getBackground())
    }

    private fun collides(a: RectF,b:RectF):Boolean {
        return (axis.isIntersecting(a, b))
    }

    fun resetCanvas(){
        synchronized(entity.getQuads()) {
            for (i in 0 until entity.getQuads().size) {

                entity.setColor(i, ColorRGBA.transparent,0.01f)

            }
        }
    }

    fun getEntity():MnistEntity{
        return entity
    }

    override fun draw(batch: Batch) {
        entity.draw(batch)
    }

    override fun update(delta: Long) {

    }

    fun getEvent():MultiTouchEvent?{
        return event
    }


}