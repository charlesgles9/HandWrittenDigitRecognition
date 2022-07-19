package com.graphics.glcanvas.engine.utils

class SpriteSheet( private var WIDTH: Int,  private var HEIGHT: Int) {

    private var position=0
    private var frames=ArrayList<FloatArray>()

    init {
        initialize()
    }

    private fun initialize(){
        for(j in 0 until HEIGHT)
            for(i in 0 until WIDTH )
                frames.add(getSTMatrix(i,j))
    }

    fun resize(WIDTH:Int,HEIGHT:Int){
        this.WIDTH=WIDTH
        this.HEIGHT=HEIGHT
        frames.clear()
        initialize()
    }

    fun resize(size:Int){
        frames.clear()
        for(i in 0 until size){
            frames.add(getSTMatrix(1,1))
        }
    }

    fun clone():SpriteSheet{
        val copy=SpriteSheet(1,1)
            copy.resize(frames.size)
         for(i in 0 until frames.size)
            copy.setSTMatrix(frames[i],i)
       return copy
    }
    fun getCurrentFrame():FloatArray{
        return frames[position]
    }

    fun getFrameAt(position:Int):FloatArray{
        return frames[position]
    }

    fun setCurrentFrame(position: Int){
        this.position=position
    }

    //fill the texture matrix with the ST values
    private fun getSTMatrix(row:Int,col:Int):FloatArray{
        val matrix=FloatArray(8)
        setSTMatrix(row, col, matrix)
        return matrix
    }

    private fun setSTMatrix(row:Int,col:Int,matrix:FloatArray){
        val fSizeX=1.0f/WIDTH
        val fSizeY=1.0f/HEIGHT
        val originS=row*fSizeX
        val originT=col*fSizeY
        matrix[0]=originS
        matrix[1]=originT+fSizeY
        matrix[2]=originS
        matrix[3]=originT
        matrix[4]=originS+fSizeX
        matrix[5]=originT
        matrix[6]=originS+fSizeX
        matrix[7]=originT+fSizeY

    }
    private fun setSTMatrix(copy:FloatArray,index: Int){
        val matrix= frames[index]
        matrix[0]=copy[0]
        matrix[1]=copy[1]
        matrix[2]=copy[2]
        matrix[3]=copy[3]
        matrix[4]=copy[4]
        matrix[5]=copy[5]
        matrix[6]=copy[6]
        matrix[7]=copy[7]

    }
    fun setSTMatrix(row:Float,col:Float,width:Float,height:Float,scaleW:Float,scaleH:Float,index:Int){
        WIDTH=width.toInt()
        HEIGHT=height.toInt()
        val fSizeX=width/scaleW
        val fSizeY=height/scaleH
        val originS=row/scaleW
        val originT=col/scaleH
        val matrix:FloatArray= frames[index]
        matrix[0]=originS
        matrix[1]=originT+fSizeY
        matrix[2]=originS
        matrix[3]=originT
        matrix[4]=originS+fSizeX
        matrix[5]=originT
        matrix[6]=originS+fSizeX
        matrix[7]=originT+fSizeY

    }

}