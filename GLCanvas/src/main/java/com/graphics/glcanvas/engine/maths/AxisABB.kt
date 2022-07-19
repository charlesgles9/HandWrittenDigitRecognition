package com.graphics.glcanvas.engine.maths

import com.graphics.glcanvas.engine.structures.RectF

open class AxisABB {

     fun isIntersecting(Ax:Float,Ay:Float,Aw:Float,Ah:Float,
                       Bx:Float,By:Float,Bw:Float,Bh:Float):Boolean{
        return axis(Ax,Bx,(Aw+Bw)*0.5f)&&
               axis(Ay,By,(Ah+Bh)*0.5f)
    }

     fun isIntersecting(a:RectF,b:RectF):Boolean{
       return isIntersecting(a.getX(),a.getY(),a.getWidth(),a.getHeight(),
                b.getX(),b.getY(),b.getWidth(),b.getHeight())
    }

    private fun axis(centerA:Float,centerB:Float,sizeAB:Float):Boolean{
        return centerA>=(centerB-sizeAB)&&
                centerA<=(centerB+sizeAB)
    }
}