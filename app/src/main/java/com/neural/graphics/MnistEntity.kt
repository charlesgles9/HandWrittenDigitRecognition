package com.neural.graphics

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.graphics.mnistReader.MnistMatrix


class MnistEntity(private val matrix: MnistMatrix,private val position:Vector2f,private val pixelSize:Float) :Update{

    private val grids= MutableList(matrix.numberOfColumns*matrix.numberOfRows,init ={RectF(0f,0f,0f,0f)})
    private val background=RectF(position.x,position.y,10f,10f)
    private val expectedOutput= MutableList(10,init = {0.0})

    fun getGridValue(index:Int):Double{
        return grids[index].getColor(0).get(0).toDouble()
    }

    fun getQuads():MutableList<RectF>{
        return grids
    }

    fun setColor(index: Int, color: ColorRGBA,alpha:Float){
        color.set(alpha,alpha,alpha,alpha)
        grids[index].setColor(color)
        matrix.setValue((index)/28,(index)%28,alpha.toInt())
    }

    fun getExpectedOutput():MutableList<Double>{
        return expectedOutput
    }
    fun size():Int{
        return grids.size
    }

    fun getBackground():RectF{
        return background
    }
    fun getMatrix():MnistMatrix{
        return matrix
    }
     fun initGrid(matrix: MnistMatrix){
        background.setWidth(matrix.numberOfColumns*pixelSize)
        background.setHeight(matrix.numberOfRows*pixelSize)
        background.set(background.getWidth()*0.5f+position.x,
            background.getHeight()*0.5f+position.y)
        background.setThickness(20f)
        background.setColor(ColorRGBA(0.3f,0.5f,0.5f,0.5f))
        for (r in 0 until matrix.numberOfRows) {
            for (c in 0 until matrix.numberOfColumns) {
                val value=matrix.getValue(r,c)/255f
                val index=r*matrix.numberOfColumns+c
                val rect= grids[index]
                rect.set(position.x+c*pixelSize,position.y+r*pixelSize)
                rect.setWidth(pixelSize)
                rect.setHeight(pixelSize)
                rect.setColor(ColorRGBA(value))
            }

        }
         for(i in 0 until 10){
             expectedOutput[i] = if(matrix.label==i)1.0 else 0.0
         }
    }

    override fun draw(batch: Batch) {
        batch.draw(background)
        grids.forEach {
            batch.draw(it)
        }
    }

    override fun update(delta: Long) {

    }


}