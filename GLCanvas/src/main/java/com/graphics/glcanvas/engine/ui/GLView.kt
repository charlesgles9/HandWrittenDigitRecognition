package com.graphics.glcanvas.engine.ui

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Touch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.AxisABB
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.RectF
import com.graphics.glcanvas.engine.structures.Text
import com.graphics.glcanvas.engine.utils.Texture
import com.graphics.glcanvas.engine.utils.TextureAtlas
import kotlin.math.abs
import kotlin.math.min


open class GLView(width:Float,height:Float) :GLLayoutParams(width, height),Update, Touch{

    /*helps us specify the foreground objects position
     relative to the background image this will help us create progress bars */
      private var fOffset=Vector2f()
      protected var isProgressBar=false
      protected var horizontalBar=false
      protected var maxProgressBar=100f
      protected var currentProgress=0f
      protected var backgroundThickness=0f
      private var connerRadius=0f
     // view position
      private val position=Vector2f()
      private var background= RectF(0f,0f,width, height)
      private var foreground=RectF(0f,0f,width, height)
    //progress bar thumb
      private var pThumb=RectF(0f,0f,10f,10f)
    // click effect color
      private var ripple=ColorRGBA()
    // default color no click
      private var default=ColorRGBA()
      private var collision=AxisABB()
      protected var atlas: TextureAtlas?=null
      protected var text:Text?=null
      private val onClickEvents= mutableListOf<OnClickEvent>()
      private val onMultiTouchEvents= mutableListOf<MultiTouchEvent>()
    // layout constraints
      private val constraint=LayoutConstraint(this)
    //primary and secondary texture coordinates for click effects
      private var tp=""
      private var ts=""
      private var tpIndex=0
      private var tsIndex=0
    // checkbox variables
      private var check=false
      protected var isCheckBox=false
      private  var clicked=false
     // finger size or touch area
      private val thumb=20f
      private var centerText=true
      private var visible=true
      private var enabled=true
    // for touch events
      private var enableTouchEvents=true
      init {
          foreground.setColor(ColorRGBA(0f,0f,0f,0f))
          background.setWidth(width)
          background.setHeight(height)
          foreground.setWidth(width)
          foreground.setHeight(height)
          pThumb.setColor(ColorRGBA.transparent)
      }

      fun setBackgroundColor(color: ColorRGBA){
            background.setColor(color)
            default.set(color)
      }

      protected fun setDefaultColor(color:ColorRGBA){
          default.set(color)
      }

      fun clipViewUpper(x:Float,y:Float){
          text?.setClipUpper(x,y)
          background.setClipUpper(x,y)
          foreground.setClipUpper(x, y)
     }


    fun clipViewLower(x:Float,y:Float){
        text?.setClipLower(x,y)
        background.setClipLower(x,y)
        foreground.setClipLower(x,y)
    }

    fun getTextView():Text?{
        return text
    }

      fun setBackgroundTextureAtlas(atlas: TextureAtlas){
          background.setTextureAtlas(atlas)
      }

     fun setForegroundTextureAtlas(atlas: TextureAtlas){
        foreground.setTextureAtlas(atlas)
     }

     fun setProgressThumbColor(color: ColorRGBA){
         pThumb.setColor(color)
     }

     fun setProgressRounded(radius:Float){
        pThumb.setConnerRadius(radius)
     }

    open fun setEnableTouchEvents(enable: Boolean){
        enableTouchEvents=enable
    }

    fun isTouchEventsEnabled():Boolean{
        return enableTouchEvents
    }

    fun setWidthPixels(width: Float){
        background.setWidth(width)
        foreground.setWidth(width)
        this.width=width
    }
    fun setHeightPixels(height: Float){
        background.setHeight(width)
        foreground.setHeight(width)
        this.height=height
    }
     fun setTexture(path:String){
         background.getSpriteSheet().resize(1,1)
         background.setTexture(Texture(path))
         foreground.setColor(ColorRGBA.transparent)
     }

     fun setForegroundColor(color: ColorRGBA){
        this.foreground.setColor(color)
      }

     fun setBackgroundSubTexture(name:String, index:Int){
        if(atlas!=null)
            background.setSubTextureAtlas(atlas!!, name, index)
      }

     fun setForegroundSubTexture(name:String, index: Int){
        if(atlas!=null)
            foreground.setSubTextureAtlas(atlas!!,name, index)
    }

     fun setSecondaryImage(ts:String,index: Int){
        this.ts=ts
        this.tsIndex=index
    }

     fun setPrimaryImage(tp:String,index: Int){
        this.tp=tp
        this.tpIndex=index
    }

    fun setRippleColor(color: ColorRGBA){
        this.ripple.set(color)
    }

    open fun roundedCorner(value:Float){
        getBackground().setConnerRadius(value)
        getForeground().setConnerRadius(value)
        connerRadius=value
    }

    fun getCornerRadius():Float{
        return connerRadius
    }
    open fun setVisibility(visible:Boolean){
         this.visible=visible
         text?.setVisibility(visible)
    }

    fun isVisible():Boolean{
        return visible
    }

    open fun setEnabled(enable:Boolean){
        this.enabled=enable
    }

    fun isEnabled():Boolean{
        return enabled
    }
      open fun set(x:Float, y:Float){
          position.set(x,y)
      }

      open fun setX(x:Float){
          position.x=x
      }

      fun setY(y:Float){
          position.y=y
      }

    open fun setZ(z:Float){
        background.setZ(z)
        foreground.setZ(z)
    }

    protected fun positionBars(horizontal: Boolean,progress:Float,max:Float){
        if(horizontal) {
            getForeground().setHeight(height*0.8f)
            getForeground().setWidth(width * ((progress+1) / (max+1)))
            fOffset.x=((getForeground().getWidth()-width)*0.5f)
        }else{
            getForeground().setWidth(width*0.8f)
            getForeground().setHeight(height*((progress+1)/(max+1)))
            fOffset.y=((height-getForeground().getHeight())*0.5f)
        }
    }



    //calculate the progress based on where the user has clicked
    private fun progressChanged(it:Vector2f){
        val pw= min(abs(position.x-width/2-it.x),width).toInt()
        val ph=min(abs(position.y-height/2-it.y),height).toInt()
        // position the horizontal bar
        if(horizontalBar&&pw!=getForeground().getWidth().toInt()) {
            currentProgress = pw / width * maxProgressBar
            getForeground().setWidth(pw.toFloat() - backgroundThickness *1f)
            fOffset.x = ((width - pw.toFloat()) * -0.5f)
            pThumb.set(getForeground().getX()+getForeground().getWidth()*0.5f,
                getForeground().getY())
            //position the vertical bar
        }else if(!horizontalBar&&ph!=getForeground().getHeight().toInt()){
            currentProgress = pw / width * maxProgressBar
            getForeground().setHeight(ph.toFloat() - backgroundThickness *1f)
            fOffset.y = ((height - ph.toFloat()) * -0.5f)
            pThumb.set(getForeground().getX(),
                getForeground().getY()+getForeground().getHeight()*0.5f)
        }
    }

      override fun draw(batch: Batch) {
          constraint.applyConstraints()
        //  applyMargin()
          // update click events before drawing
          onClickEvents.forEach {
              clicked = it.getPointerDown()
              // if view is a progressbar calculate the progress
              if (clicked&&isProgressBar&&position.x!=-1f&&position.y!=-1f) {
                 progressChanged(it.getPosition())
              }
          }
          // if there's a multi touch event instead
          onMultiTouchEvents.forEach {
              clicked=it.getTouchDown()

          }
          background.set(position.x,position.y)
          foreground.set(position.x+fOffset.x,position.y+fOffset.y)
          if(visible) {
              batch.draw(background)
              batch.draw(foreground)

              // center the text if available
              val tw = (text?.width?.times(0.5f) ?: 0f)
              val th = (text?.height?.times(0.5f) ?: 0f)
              if (centerText)
                  text?.set(position.x - tw, position.y - th, getZ())
              else
                  text?.set(position.x - width * 0.45f, position.y - height * 0.45f, getZ())

              //clips text that are out of the viewing area of this entity
              LayoutConstraint.clipView(position.x,
                  position.y,
                  width,
                  height,
                  text?.position?.x ?: 0f,
                  text?.position?.y ?: 0f,
                  text?.width ?: 0f,
                  text?.height ?: 0f,
                  text)
              text?.draw(batch)
          }
         //click events for checkbox
          if(isCheckBox)
              checkBoxToggle(check)
          else
          //click effects for views
              changeTextureAndColors(clicked)

      }

    fun setCenterText(centerText:Boolean){
        this.centerText=centerText
    }
    fun setChecked(check:Boolean){
        this.check=check
    }

    fun getChecked():Boolean{
        return check
    }
    private fun changeTextureAndColors(flag:Boolean){
        if(flag){
            background.setColor(ripple)
            if(ts.isNotEmpty())
                setBackgroundSubTexture(ts,tsIndex)
        }else {
            background.setColor(default)
            if (tp.isNotEmpty())
                setBackgroundSubTexture(tp,tpIndex)
        }
    }
    private fun checkBoxToggle(flag:Boolean){
        if(flag){
            foreground.setColor(ripple)
            if(ts.isNotEmpty())
                setForegroundSubTexture(ts,tsIndex)
        }else {
            foreground.setColor(default)
            if (tp.isNotEmpty())
                setForegroundSubTexture(tp,tpIndex)
        }
    }


    fun clearOnClick(){
        onClickEvents.clear()
    }

    protected fun getBackground():RectF{
        return background
    }

    protected fun getForeground():RectF{
        return foreground
    }

    override fun update(delta: Long) {

    }

   fun getX():Float{
       return position.x
   }

    fun getY():Float{
        return position.y
    }

    fun getZ():Float{
        return background.getZ()
    }
    fun getConstraints():LayoutConstraint{
        return constraint
    }

    fun getThumbSize():Float{
        return thumb
    }


    fun contains(x:Float,y:Float):Boolean{
        return collision.isIntersecting(background.getX(),
                                 background.getY(),
                                 background.getWidth(),
                                 background.getHeight(),x,y,thumb,thumb)

    }
    fun contains(view:GLView):Boolean{
        return collision.isIntersecting(background.getX(),
            background.getY(),
            background.getWidth(),
            background.getHeight(),view.getX(),view.getY(),view.width,view.height)

    }
    fun setOnClickListener(onclick: OnClickEvent.OnClickListener){
        onClickEvents.add(OnClickEvent(onclick,this))
    }

    fun setMultiTouchListener(touch:MultiTouchEvent.OnMultiTouchListener){
        onMultiTouchEvents.add(MultiTouchEvent(touch,background))
    }

    override fun onTouchEvent(event: MotionEvent):Boolean{

        if(enableTouchEvents){
        onMultiTouchEvents.forEach {
            if(enabled&&isVisible())
                it.onTouchEvent(event)
          }
            onClickEvents.forEach {
                if (enabled && isVisible())
                    it.onTouchEvent(event)

            }

        }
        return true
    }




}