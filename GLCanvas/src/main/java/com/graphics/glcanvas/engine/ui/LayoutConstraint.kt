package com.graphics.glcanvas.engine.ui

import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Text
import kotlin.math.abs
import kotlin.math.max

open class LayoutConstraint(private val view:GLView) : Constraints() {

    private var left:GLView?=null
    private var right:GLView?=null
    private var above:GLView?=null
    private var below:GLView?=null
    private var center:GLView?=null
    private var end:GLView?=null
    private var center_Horizontal:GLView?=null
    private var center_Vertical:GLView?=null
    private var MARGIN= floatArrayOf(0f,0f,0f,0f)


     fun getView():GLView{
         return view
     }

     override fun toLeftOf(view: GLView){
        this.left=view
    }

    override fun toRightOf(view:GLView){
        this.right=view
    }

    override fun alignAbove(view:GLView){
        this.above=view
    }

    override fun alignBelow(view:GLView){
        this.below=view
    }

    override fun alignCenter(view:GLView){
        this.center=view
    }

    override fun alignEnd(view:GLView){
        this.end=view
    }
     override fun alignCenterVertical(view:GLView){
         this.center_Vertical=view
     }
     override fun alignCenterHorizontal(view:GLView){
         this.center_Horizontal=view
     }
    override fun layoutMarginLeft(margin:Float){
        MARGIN[0]=margin
    }

    override fun layoutMarginRight(margin: Float){
        MARGIN[1]=margin
    }

    override fun layoutMarginTop(margin: Float){
        MARGIN[2]=margin
    }

    override fun layoutMarginBottom(margin: Float){
        MARGIN[3]=margin
    }

    private fun applyLeft(){
     val width=(left?.width?:0f)
     val lx= (left?.getX()?.minus(width*0.5f+view.width*0.5f)?:view.getX())
             view.set(lx,view.getY())
    }

    private fun applyRight(){
        val width=(right?.width?:0f)
        val lx= (right?.getX()?.plus(width*0.5f+view.width*0.5f+getMarginLeft()-getMarginRight())?:view.getX())
        view.set(lx,view.getY())
    }

    private fun applyAbove(){
        val height=(above?.height?:0f)
        val ly= (above?.getY()?.minus(height*0.5f+view.height*0.5f)?:view.getY())
        view.set(view.getX(),ly)
    }

    private fun applyEnd(){
        val width=(end?.width?:0f)
        val lx= (end?.getX()?.plus(width*0.5f-view.width*0.5f+getMarginLeft()-getMarginRight())?:view.getX())
        view.set(lx,view.getY())
    }
    private fun applyBelow(){
        val height=(below?.height?:0f)
        val ly= (below?.getY()?.plus(height*0.5f+view.height*0.5f+getMarginBottom())?:view.getY())
        view.set(view.getX(),ly)
    }

    private fun applyCenter(){
        val lx= (center?.getX()?:view.getX())
        val ly= (center?.getY()?:view.getY())
        view.set(lx,ly)
    }


     private fun applyCenterVertical(){
             val ly= (center_Vertical?.getY()?:view.getY())
             view.set(view.getX(),ly)
     }

     private fun applyCenterHorizontal(){
             val lx= (center_Horizontal?.getX()?:view.getX())
             view.set(lx,view.getY())
     }

    override fun getMarginLeft():Float{
        return MARGIN[0]
    }

    override fun getMarginRight():Float{
         return MARGIN[1]
    }

    override fun getMarginTop():Float{
         return MARGIN[2]
    }

    override fun getMarginBottom():Float{
         return MARGIN[3]
     }

    override fun applyConstraints(){
        applyLeft()
        applyRight()
        applyAbove()
        applyBelow()
        applyCenter()
        applyEnd()
        applyCenterVertical()
        applyCenterHorizontal()
    }
    companion object {
        fun clipView(parentX:Float,parentY:Float,parentW:Float,parentH:Float
                     ,viewX:Float,viewY:Float,viewW:Float,viewH:Float,text:Text?){
            //account for layout margins
            //y axis clip test
            val lowerVisibleY=parentY+parentH*0.5f>viewY-viewH
            val upperVisibleY=parentY-parentH*0.5f<viewY+viewH
            //x axis clip test
            val lowerVisibleX=parentX+parentW*0.5f>viewX-viewW
            val upperVisibleX=parentX-parentW*0.5f<viewX+viewW

            text?.setClipLower(parentX + (parentW * 0.5f), parentY + (parentH * 0.5f))
            text?.setClipUpper(parentX - parentW* 0.5f, parentY - parentH * 0.5f)

        }
         fun clipView(parent:GLView,view:GLView){
             //account for layout margins
             val topBottom=view.getConstraints().getMarginTop()-view.getConstraints().getMarginBottom()
             val leftRight=view.getConstraints().getMarginLeft()-view.getConstraints().getMarginRight()
             val mHeight=view.height*0.5f-topBottom
             val mWidth=view.width*0.5f-leftRight
            //y axis clip test
            val lowerVisibleY=parent.getY()+parent.height*0.5f>view.getY()-mHeight
            val upperVisibleY=parent.getY()-parent.height*0.5f<view.getY()+mHeight
            //x axis clip test
            val lowerVisibleX=parent.getX()+parent.width*0.5f>view.getX()-mWidth
            val upperVisibleX=parent.getX()-parent.width*0.5f<view.getX()+mWidth
            val visible=lowerVisibleY && upperVisibleY&&upperVisibleX&&lowerVisibleX
          //  view.setVisibility(visible&&view.isVisible())
            view.clipViewLower(parent.getX() + (parent.width * 0.5f), parent.getY() + (parent.height * 0.5f))
            view.clipViewUpper(parent.getX() - parent.width * 0.5f, parent.getY() - parent.height * 0.5f)

        }

        // in case this view is a nested layout pass in the margin information or
        // we'll get inaccurate clipping calculations
        fun clipView(parent:GLView,inner:GLView,view:GLView){
            //account for layout margins
            val topBottom=inner.getConstraints().getMarginTop()-inner.getConstraints().getMarginBottom()+
                    parent.getConstraints().getMarginTop()-parent.getConstraints().getMarginBottom()
            val leftRight=inner.getConstraints().getMarginLeft()-inner.getConstraints().getMarginRight()+
                    parent.getConstraints().getMarginLeft()-parent.getConstraints().getMarginRight()
            val mHeight=view.height*0.5f-topBottom
            val mWidth=view.width*0.5f-leftRight
            //y axis clip test
            val lowerVisibleY=parent.getY()+parent.height*0.5f>view.getY()-mHeight
            val upperVisibleY=parent.getY()-parent.height*0.5f<view.getY()+mHeight
            //x axis clip test
            val lowerVisibleX=parent.getX()+parent.width*0.5f>view.getX()-mWidth
            val upperVisibleX=parent.getX()-parent.width*0.5f<view.getX()+mWidth
            val visible=lowerVisibleY && upperVisibleY&&upperVisibleX&&lowerVisibleX
          //  view.setVisibility(visible&&view.isVisible())
            view.clipViewLower(parent.getX() + (parent.width * 0.5f), parent.getY() + (parent.height * 0.5f)-view.getConstraints().getMarginBottom())
            view.clipViewUpper(parent.getX() - parent.width * 0.5f, parent.getY() - parent.height * 0.5f+view.getConstraints().getMarginTop())

        }
        fun groupItems(offset: Vector2f, parent: GLView, items: MutableList<GLView>, rows:Int, cols:Int){
                 offset.set(parent.getX()-parent.width*0.5f,parent.getY()-parent.height*0.5f)
            // layout warps around the items
                 var maxWidth=parent.width
                 var maxHeight=parent.height
               for(r in 0 until rows){
                   for(c in 0 until cols){
                       val index=r*cols+c
                       if(index>=items.size) return
                       val child=items[index]
                       val topBottom=child.getConstraints().getMarginTop()-child.getConstraints().getMarginBottom()
                       val leftRight=child.getConstraints().getMarginLeft()-child.getConstraints().getMarginRight()
                       val width=child.width
                       val height=child.height
                       child.set(offset.x+(width+leftRight)*0.5f+(width)*c,offset.y+(height+topBottom)*0.5f+(height+topBottom)*r)
                       maxWidth= max(child.getX()-offset.x+width,maxWidth)
                       maxHeight=max(child.getY()-offset.y+height,maxHeight)
                   }
               }
              parent.setWidthPixels(maxWidth)
              parent.setHeightPixels(maxHeight)

        }
         fun groupItems(
            orientation: Int,
            offset: Vector2f,
            parent: GLView,
            items: MutableList<GLView>
        ) {
            if (orientation == LinearLayoutConstraint.VERTICAL) {
                for (i in 0 until items.size) {
                    val view = items[i]
                    val xOffset = parent.getX() - parent.width * 0.5f
                    //if its the first item position it at the top
                    if (i == 0)
                        view.set(
                            xOffset + view.width * 0.5f+view.getConstraints().getMarginLeft()-
                                      view.getConstraints().getMarginRight(),
                            parent.getY() - parent.height * 0.5f + view.height * 0.5f - view.getConstraints()
                                .getMarginBottom() + offset.y+view.getConstraints().getMarginTop()
                        )
                    // set next layout below this layout
                    if (view != items.last()) {
                        val next = items[i + 1]
                        next.setX(xOffset + next.width * 0.5f+next.getConstraints().getMarginLeft()-
                                   next.getConstraints().getMarginRight())
                        next.getConstraints().alignBelow(view)
                        next.setY(next.getY()-next.getConstraints().getMarginBottom()+next.getConstraints().getMarginTop())
                    }
                }
                // horizontal orientation code from left to right
            } else {
                for (i in 0 until items.size) {
                    val view = items[i]
                    val yOffset = parent.getY() - parent.height * 0.5f
                    //if its the first item position it at the top
                    if (i == 0)
                        view.set(
                            parent.getX() - parent.width * 0.5f + view.width * 0.5f + view.getConstraints()
                                .getMarginRight() + offset.x,
                            yOffset + view.height * 0.5f + view.getConstraints()
                                .getMarginBottom()
                        )
                    // set next layout below this layout
                    if (view != items.last()) {
                        val next = items[i + 1]
                        next.setY(
                            yOffset + next.height * 0.5f + view.getConstraints()
                                .getMarginBottom()
                        )
                        next.getConstraints().toRightOf(view)
                    }
                }
            }
        }
    }

 }