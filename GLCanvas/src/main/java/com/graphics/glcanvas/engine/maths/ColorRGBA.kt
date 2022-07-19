package com.graphics.glcanvas.engine.maths

class ColorRGBA {

    companion object{
        const val R:Int=0
        const val G:Int=1
        const val B:Int=2
        const val A:Int=3
        val transparent=ColorRGBA(0f,0f,0f,0f)
        val red=ColorRGBA(1f,0f,0f,1f)
        val white=ColorRGBA(1f,1f,1f,1f)
        val blue=ColorRGBA(0f,0f,1f,1f)
        val green=ColorRGBA(0f,1f,0f,1f)
    }




    private var data=arrayOf(1.0f,1.0f,1.0f,1.0f)
    constructor( value:Float)  {
        set(value,value,value,value)
    }
    constructor()  {}
    constructor(r:Float,g:Float,b:Float,a:Float){
          set(r,g,b,a)
    }
    constructor(color: ColorRGBA){
        set(color)
    }

    fun set(r:Float,g:Float,b:Float,a:Float){
        data[R]=r
        data[G]=g
        data[B]=b
        data[A]=a
    }

    fun set(r:Float,g: Float,b: Float){
        data[R]=r
        data[G]=g
        data[B]=b
    }

    fun setAlpha(a: Float){
        data[A]=a
    }

    fun set(color:ColorRGBA){
        set(color.get(R),color.get(G),color.get(B),color.get(A))
    }

    fun get(value:Int):Float{
       return data[value]
    }

    fun getData():Array<Float>{
        return data
    }

    fun mix(color:ColorRGBA){
        data[R]=(data[R]*color.get(R))
        data[G]=(data[G]*color.get(G))
        data[B]=(data[B]*color.get(B))

    }

    fun multiply(scalar:Float){
        data[R]=(data[R]*scalar)
        data[G]=(data[G]*scalar)
        data[B]=(data[B]*scalar)
        data[A]=(data[A]*scalar)
    }

}