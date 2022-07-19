package com.graphics.glcanvas.engine

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import com.graphics.glcanvas.engine.constants.Primitives
import com.graphics.glcanvas.engine.maths.Vector3f
import com.graphics.glcanvas.engine.structures.*
import com.graphics.glcanvas.engine.ui.ScreenRatio
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.abs


class Batch() {

    // a model matrix used to move models from object space
    private val mModelMatrix=FloatArray(16)
    // transforms world space into eye space
    private val mViewMatrix= FloatArray(16)
    // model view Projection  matrix this we will pass it to the vertex shader
    private val mMVPMatrix=FloatArray(16)

    // buffers
    // positional
    private var vertexBuffer:FloatBuffer?=null
    //indices
    private var drawListBuffer:ShortBuffer?=null
    //color
    private var colorBuffer:FloatBuffer?=null
    //texture
    private var textureBuffer:FloatBuffer?=null
    //center buffer for circles and quads
    private var centerBuffer:FloatBuffer?=null
    // trim buffer to cut a quad or a shape
    private var clipBuffer:FloatBuffer?=null

    // sends the extra quad data that will enable us to
    // create rounded edges
    private var roundedPropBuffer:FloatBuffer?=null
    private var transformBuffer:FloatBuffer?=null
    private var textOptionsBuffer:FloatBuffer?=null
    //vertex count
    private var vcount=0
    //index count
    private var icount=0
    //color count
    private var acount=0
    //texture count
    private var tcount=0
    //center count
    private var mcount=0
    // rounded properties count
    private var rcount=0
    //trim count
    private var qcount=0
    //transform count
    private var ncount=0
    //textOptions count
    private var ocount=0
    // draw calls counter
    private var num_draw_calls=0
    private var num_triangles=0

    private val VERTEX_COORDS_PER_VERTEX=3
    private val COLOR_COORDS_PER_VERTEX=4
    private val TEXTURE_COORDS_PER_VERTEX=2
    private val BYTES_PER_FLOAT=4

    private val position=Vector3f()
    private val rotation=Vector3f()

    // mesh data
    private val BATCH_SIZE=1000
    private var vertexes= FloatArray(BATCH_SIZE*VERTEX_COORDS_PER_VERTEX*BYTES_PER_FLOAT)
    private var indices=ShortArray(BATCH_SIZE*6)
    private var colors=FloatArray(BATCH_SIZE*COLOR_COORDS_PER_VERTEX*BYTES_PER_FLOAT)
    private var textures=FloatArray(BATCH_SIZE*TEXTURE_COORDS_PER_VERTEX*BYTES_PER_FLOAT)
    private var transforms=FloatArray(BATCH_SIZE*6*BYTES_PER_FLOAT)
    private var textOptions=FloatArray(BATCH_SIZE*8*BYTES_PER_FLOAT)
    // current texture
    private var mTexture=0
    // text uniforms
    private var isText=false
    // used to draw batched circles since we need the center position
    // also useful to pass in the center position of our quad to create rounded edges
    private var centerVertex=FloatArray(BATCH_SIZE*4*BYTES_PER_FLOAT)
    private var roundedRectProperties=FloatArray(BATCH_SIZE*2*BYTES_PER_FLOAT)
    private var clipAttribute=FloatArray(BATCH_SIZE*4*BYTES_PER_FLOAT)
    private val buffers=IntArray(8)
    private val defaultShader=Shader("shaders/default_vertex_shader.glsl","shaders/default_fragment_shader.glsl")
    private var camera:Camera2D?=null
    private val batchQueue=BatchQueue()
    private var primitiveType= Primitives.QUAD
    private val entities=ArrayList<Vertex>()

    init {
        GLES32.glGenBuffers(7,buffers,0)
        createVertexBuffer()
        createColorBuffer()
        createTextureBuffer()
        createCenterBuffer()
        createRoundedPropertiesBuffer()
        createTrimBuffer()
        createTransformBuffer()
        initializeDrawList()
        createTextOptionsBuffer()
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,1)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,2)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,3)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,4)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,5)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,6)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,7)
    }
    
    fun begin(camera: Camera2D){
        this.camera=camera
        batchQueue.reset()
        reset()
    }

    fun resetStats(){
        num_draw_calls=0
        num_triangles=0
    }

    fun setMode(mode:Int){
        batchQueue.setMode(mode)
    }

    fun getCamera():Camera2D?{
        return camera
    }

    private fun reset(){
        this.entities.clear()
        vcount=0
        acount=0
        icount=0
        tcount=0
        mcount=0
        rcount=0
        qcount=0
        ncount=0
        ocount=0
    }

    fun draw(vertex: Vertex){
        if(vertex.getVisibility())
        entities.add(vertex)
    }

    fun getDrawCallCount():Int{
        return num_draw_calls
    }

    fun getTriangleCount():Int{
        return num_triangles
    }

    fun end(){

        for( i in 0 until entities.size){
            val entity=entities[i]
            var type= Primitives.CIRCLE
            when (entity) {
                is RectF ->
                    type = Primitives.QUAD
                is Line ->
                    type= Primitives.LINE
                is PolyLine ->
                    type= Primitives.POLYLINE
                is Polygon ->
                    type= Primitives.TRIANGLE
                is Circle ->
                    type = Primitives.CIRCLE
            }
             batchQueue.addVertex(entity,type)
        }


        while (!batchQueue.getBatchedQueue().isEmpty()){
            val bucket=batchQueue.getBatchedQueue().remove()
            val list=bucket.getBatchList()
            reset()
            isText=list.first() is Character
             if(isText){
                 val char=list.first() as Character
                applyDistanceFields(char.getBorderWidth(),char.getBorderEdge(),
                                    char.getInnerWidth(),char.getInnerEdge(),
                                    char.getOutlineColor().get(0),char.getOutlineColor().get(1),
                                    char.getOutlineColor().get(2),char.getOutlineColor().get(3))
             }
               var i=0
               for(vertex in list){
                   val count=i*4*4
                   if(count>=BATCH_SIZE*12*0.9f){
                       primitiveType = when(bucket.getPrimitiveType()){
                           Primitives.QUAD -> Primitives.QUAD
                           Primitives.TRIANGLE -> Primitives.TRIANGLE
                           Primitives.CIRCLE -> Primitives.CIRCLE
                           Primitives.LINE -> Primitives.LINE
                           Primitives.POLYLINE -> Primitives.POLYLINE
                       }
                       draw()
                       reset()
                       i=0

                   }
                   when(bucket.getPrimitiveType()){
                       Primitives.QUAD ->  addRectF(i, vertex)
                       Primitives.TRIANGLE -> addPolygon(i, vertex)
                       Primitives.CIRCLE ->  addCircle(i,vertex)
                       Primitives.LINE ->  addLine(i,vertex)
                       Primitives.POLYLINE -> addPolyLine(i, vertex)
                   }
                   i++
               }
            if(i!=0) {
                primitiveType = when(bucket.getPrimitiveType()){
                    Primitives.QUAD -> Primitives.QUAD
                    Primitives.TRIANGLE -> Primitives.TRIANGLE
                    Primitives.CIRCLE -> Primitives.CIRCLE
                    Primitives.LINE -> Primitives.LINE
                    Primitives.POLYLINE -> Primitives.POLYLINE
                }
                draw()
            }

        }
    }


    private fun applyDistanceFields(be:Float,bw:Float,ie:Float,iw:Float,r:Float,g:Float,b:Float,a:Float){
        for(i in 0 until 4) {
            textOptions[ocount++] = be
            textOptions[ocount++] = bw
            textOptions[ocount++] = ie
            textOptions[ocount++] = iw
            textOptions[ocount++] = r
            textOptions[ocount++] = g
            textOptions[ocount++] = b
            textOptions[ocount++] = a
        }
    }

    private fun addCircle(index:Int,vertex: Vertex){
        val rect= vertex as Circle
        val sizeX=rect.getRadius()
        val sizeY=rect.getRadius()
        val x=rect.getX()
        val y=rect.getY()
        val z=rect.getZ()
        for(i in 0 until  4) {
            transforms[ncount++] = Math.toRadians(vertex.getAngleX().toDouble()).toFloat()
            transforms[ncount++] = Math.toRadians(vertex.getAngleY().toDouble()).toFloat()
            transforms[ncount++] = Math.toRadians(vertex.getAngleZ().toDouble()).toFloat()
            transforms[ncount++] = vertex.getScale().x
            transforms[ncount++] = vertex.getScale().y
            transforms[ncount++] = 1f
        }

        //top left
        vertexes[vcount++]=-sizeX+x
        vertexes[vcount++]=sizeY+y
        vertexes[vcount++]=z
        //bottom left
        vertexes[vcount++]=-sizeX+x
        vertexes[vcount++]=-sizeY+y
        vertexes[vcount++]=z
        //bottom right
        vertexes[vcount++]=sizeX+x
        vertexes[vcount++]=-sizeY+y
        vertexes[vcount++]=z
        //top right
        vertexes[vcount++]=sizeX+x
        vertexes[vcount++]=sizeY+y
        vertexes[vcount++]=z

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        val texture=rect.getTextureCords()
        textures[tcount++]=texture[0]
        textures[tcount++]=texture[1]

        textures[tcount++]=texture[2]
        textures[tcount++]=texture[3]

        textures[tcount++]=texture[4]
        textures[tcount++]=texture[5]

        textures[tcount++]=texture[6]
        textures[tcount++]=texture[7]

        mTexture=rect.getTexture().getId()
        roundedRectProperties[rcount++]=rect.getThickness()
        roundedRectProperties[rcount++]=sizeX

        roundedRectProperties[rcount++]=rect.getThickness()
        roundedRectProperties[rcount++]=sizeX

        roundedRectProperties[rcount++]=rect.getThickness()
        roundedRectProperties[rcount++]=sizeX

        roundedRectProperties[rcount++]=rect.getThickness()
        roundedRectProperties[rcount++]=sizeX

        indices[icount++]= (index*4+0).toShort()
        indices[icount++]= (index*4+1).toShort()
        indices[icount++]= (index*4+2).toShort()
        indices[icount++]= (index*4+0).toShort()
        indices[icount++]= (index*4+2).toShort()
        indices[icount++]= (index*4+3).toShort()

        val color1=rect.getColor(0)
        val color2=rect.getColor(1)
        val color3=rect.getColor(2)
        val color4=rect.getColor(3)

        colors[acount++]=color1.get(0)
        colors[acount++]=color1.get(1)
        colors[acount++]=color1.get(2)
        colors[acount++]=color1.get(3)

        colors[acount++]=color2.get(0)
        colors[acount++]=color2.get(1)
        colors[acount++]=color2.get(2)
        colors[acount++]=color2.get(3)

        colors[acount++]=color3.get(0)
        colors[acount++]=color3.get(1)
        colors[acount++]=color3.get(2)
        colors[acount++]=color3.get(3)

        colors[acount++]=color4.get(0)
        colors[acount++]=color4.get(1)
        colors[acount++]=color4.get(2)
        colors[acount++]=color4.get(3)
        mTexture=vertex.getTexture().getId()
        num_triangles+=2
    }

    private fun addRectF(index:Int,vertex: Vertex){
        val rect= vertex as RectF
        val sizeX=rect.getWidth()*0.5f
        val sizeY=rect.getHeight()*0.5f
        val x=rect.getX()
        val y=rect.getY()
        val z=rect.getZ()
        for(i in 0 until  4) {
            transforms[ncount++] = Math.toRadians(vertex.getAngleX().toDouble()).toFloat()
            transforms[ncount++] = Math.toRadians(vertex.getAngleY().toDouble()).toFloat()
            transforms[ncount++] = Math.toRadians(vertex.getAngleZ().toDouble()).toFloat()
            transforms[ncount++] = vertex.getScale().x
            transforms[ncount++] = vertex.getScale().y
            transforms[ncount++] = 1f
        }

        //top left
        vertexes[vcount++]=-sizeX+x
        vertexes[vcount++]=sizeY+y
        vertexes[vcount++]=z
        //bottom left
        vertexes[vcount++]=-sizeX+x
        vertexes[vcount++]=-sizeY+y
        vertexes[vcount++]=z
        //bottom right
        vertexes[vcount++]=sizeX+x
        vertexes[vcount++]=-sizeY+y
        vertexes[vcount++]=z
        //top right
        vertexes[vcount++]=sizeX+x
        vertexes[vcount++]=sizeY+y
        vertexes[vcount++]=z

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        clipAttribute[qcount++]=rect.getClipUpper().x
        clipAttribute[qcount++]=rect.getClipUpper().y
        clipAttribute[qcount++]=rect.getClipLower().x
        clipAttribute[qcount++]=rect.getClipLower().y

        val texture=rect.getTextureCords()
        textures[tcount++]=texture[0]
        textures[tcount++]=texture[1]

        textures[tcount++]=texture[2]
        textures[tcount++]=texture[3]

        textures[tcount++]=texture[4]
        textures[tcount++]=texture[5]

        textures[tcount++]=texture[6]
        textures[tcount++]=texture[7]

        mTexture=rect.getTexture().getId()

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=x
        centerVertex[mcount++]=y
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        roundedRectProperties[rcount++]=rect.geThickness()
        roundedRectProperties[rcount++]=rect.getConnerRadius()

        roundedRectProperties[rcount++]=rect.geThickness()
        roundedRectProperties[rcount++]=rect.getConnerRadius()

        roundedRectProperties[rcount++]=rect.geThickness()
        roundedRectProperties[rcount++]=rect.getConnerRadius()

        roundedRectProperties[rcount++]=rect.geThickness()
        roundedRectProperties[rcount++]=rect.getConnerRadius()

        indices[icount++]= (index*4+0).toShort()
        indices[icount++]= (index*4+1).toShort()
        indices[icount++]= (index*4+2).toShort()
        indices[icount++]= (index*4+0).toShort()
        indices[icount++]= (index*4+2).toShort()
        indices[icount++]= (index*4+3).toShort()

        val color1=rect.getColor(0)
        val color2=rect.getColor(1)
        val color3=rect.getColor(2)
        val color4=rect.getColor(3)

        colors[acount++]=color1.get(0)
        colors[acount++]=color1.get(1)
        colors[acount++]=color1.get(2)
        colors[acount++]=color1.get(3)

        colors[acount++]=color2.get(0)
        colors[acount++]=color2.get(1)
        colors[acount++]=color2.get(2)
        colors[acount++]=color2.get(3)

        colors[acount++]=color3.get(0)
        colors[acount++]=color3.get(1)
        colors[acount++]=color3.get(2)
        colors[acount++]=color3.get(3)

        colors[acount++]=color4.get(0)
        colors[acount++]=color4.get(1)
        colors[acount++]=color4.get(2)
        colors[acount++]=color4.get(3)
        num_triangles+=2
    }

    private fun addLine(index: Int, vertex: Vertex){
        val line=vertex as Line
        val sizeX= abs( line.getStartX()-line.getStopX())
        val sizeY=abs(line.getStartY()-line.getStopY())
        val startx=line.getStartX()
        val starty=line.getStartY()
        val stopx=line.getStopX()
        val stopy=line.getStopY()
        val z=line.getZ()

        for(i in 0 until  2) {
            transforms[ncount++] = Math.toRadians(vertex.getAngleX().toDouble()).toFloat()
            transforms[ncount++] = Math.toRadians(vertex.getAngleY().toDouble()).toFloat()
            transforms[ncount++] = Math.toRadians(vertex.getAngleZ().toDouble()).toFloat()
            transforms[ncount++] = vertex.getScale().x
            transforms[ncount++] = vertex.getScale().y
            transforms[ncount++] = 1f
        }

        // top left
        vertexes[vcount++]=startx
        vertexes[vcount++]=starty
        vertexes[vcount++]=z
        //bottom left
        vertexes[vcount++]=stopx
        vertexes[vcount++]=stopy
        vertexes[vcount++]=z

        centerVertex[mcount++]=startx
        centerVertex[mcount++]=starty
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        centerVertex[mcount++]=startx
        centerVertex[mcount++]=starty
        centerVertex[mcount++]=sizeX
        centerVertex[mcount++]=sizeY

        clipAttribute[qcount++]=line.getClipUpper().x
        clipAttribute[qcount++]=line.getClipUpper().y
        clipAttribute[qcount++]=line.getClipLower().x
        clipAttribute[qcount++]=line.getClipLower().y

        clipAttribute[qcount++]=line.getClipUpper().x
        clipAttribute[qcount++]=line.getClipUpper().y
        clipAttribute[qcount++]=line.getClipLower().x
        clipAttribute[qcount++]=line.getClipLower().y

        indices[icount++]=(index*2+0).toShort()
        indices[icount++]=(index*2+1).toShort()

        val color1=line.getColor(0)
        val color2=line.getColor(1)

        colors[acount++]=color1.get(0)
        colors[acount++]=color1.get(1)
        colors[acount++]=color1.get(2)
        colors[acount++]=color1.get(3)

        colors[acount++]=color2.get(0)
        colors[acount++]=color2.get(1)
        colors[acount++]=color2.get(2)
        colors[acount++]=color2.get(3)
        mTexture=vertex.getTexture().getId()
    }

    private fun addPolyLine(index: Int, vertex: Vertex):Int{
        val polyLine=vertex as PolyLine
        var lcount=index
        for(i in 0 until polyLine.getPaths().size) {
            val path=polyLine.getPaths()[i]
            val x = path.getStart().x
            val y = path.getStart().y
            val z=  polyLine.getZ()
            for(j in 0 until path.getEndPoints().size){
                val line=path.getEndPoints()[j]
                val sizeX = abs(x - line.x)
                val sizeY = abs(y - line.y)
                for(r in 0 until  2) {
                    transforms[ncount++] = Math.toRadians(vertex.getAngleX().toDouble()).toFloat()
                    transforms[ncount++] = Math.toRadians(vertex.getAngleY().toDouble()).toFloat()
                    transforms[ncount++] = Math.toRadians(vertex.getAngleZ().toDouble()).toFloat()
                    transforms[ncount++] = vertex.getScale().x
                    transforms[ncount++] = vertex.getScale().y
                    transforms[ncount++] = 1f
                }
                // top left
                vertexes[vcount++] = x
                vertexes[vcount++] = y
                vertexes[vcount++] = z
                //bottom left
                vertexes[vcount++] = sizeX + x
                vertexes[vcount++] = sizeY + y
                vertexes[vcount++] = z

                centerVertex[mcount++]=x
                centerVertex[mcount++]=y
                centerVertex[mcount++]=sizeX
                centerVertex[mcount++]=sizeY

                centerVertex[mcount++]=x
                centerVertex[mcount++]=y
                centerVertex[mcount++]=sizeX
                centerVertex[mcount++]=sizeY

                clipAttribute[qcount++]=polyLine.getClipUpper().x
                clipAttribute[qcount++]=polyLine.getClipUpper().y
                clipAttribute[qcount++]=polyLine.getClipLower().x
                clipAttribute[qcount++]=polyLine.getClipLower().y

                clipAttribute[qcount++]=polyLine.getClipUpper().x
                clipAttribute[qcount++]=polyLine.getClipUpper().y
                clipAttribute[qcount++]=polyLine.getClipLower().x
                clipAttribute[qcount++]=polyLine.getClipLower().y

                indices[icount++] = (lcount * 2 + 0).toShort()
                indices[icount++] = (lcount * 2 + 1).toShort()

                val color=path.getColor(0)
                colors[acount++] =color.get(0)
                colors[acount++] =color.get(1)
                colors[acount++] =color.get(2)
                colors[acount++] =color.get(3)

                colors[acount++] =color.get(0)
                colors[acount++] =color.get(1)
                colors[acount++] =color.get(2)
                colors[acount++] =color.get(3)
                lcount++
            }

        }
        mTexture=vertex.getTexture().getId()
        return vcount

    }

    private fun addPolygon(index: Int, vertex: Vertex):Int{
        val poly=vertex as Polygon
        var pcount=index
        for(i in 0 until poly.getPaths().size) {
            val path=poly.getPaths()[i]
            val x = path.getStart().x
            val y = path.getStart().y
            val z = poly.getZ()
            if(path.getEndPoints().size>=2)
            for(j in 0 until path.getEndPoints().size step 2){
                val a=path.getEndPoints()[j]
                val b=path.getEndPoints()[j+1]
                val sizeAX = x - a.x
                val sizeAY = y - a.y
                val sizeBX = x - b.x
                val sizeBY = y - b.y
                for(r in 0 until  2) {
                    transforms[ncount++] = Math.toRadians(vertex.getAngleX().toDouble()).toFloat()
                    transforms[ncount++] = Math.toRadians(vertex.getAngleY().toDouble()).toFloat()
                    transforms[ncount++] = Math.toRadians(vertex.getAngleZ().toDouble()).toFloat()
                    transforms[ncount++] = vertex.getScale().x
                    transforms[ncount++] = vertex.getScale().y
                    transforms[ncount++] = 1f
                }
                vertexes[vcount++] = x
                vertexes[vcount++] = y
                vertexes[vcount++] = z

                vertexes[vcount++] = sizeAX + x
                vertexes[vcount++] = sizeAY + y
                vertexes[vcount++] = z

                vertexes[vcount++] = sizeBX + x
                vertexes[vcount++] = sizeBY + y
                vertexes[vcount++] = z

                centerVertex[mcount++]=x
                centerVertex[mcount++]=y
                centerVertex[mcount++]=x
                centerVertex[mcount++]=y

                centerVertex[mcount++]=x
                centerVertex[mcount++]=y
                centerVertex[mcount++]=sizeAX
                centerVertex[mcount++]=sizeAY

                centerVertex[mcount++]=x
                centerVertex[mcount++]=y
                centerVertex[mcount++]=sizeBX
                centerVertex[mcount++]=sizeBY

                clipAttribute[qcount++]=poly.getClipUpper().x
                clipAttribute[qcount++]=poly.getClipUpper().y
                clipAttribute[qcount++]=poly.getClipLower().x
                clipAttribute[qcount++]=poly.getClipLower().y

                clipAttribute[qcount++]=poly.getClipUpper().x
                clipAttribute[qcount++]=poly.getClipUpper().y
                clipAttribute[qcount++]=poly.getClipLower().x
                clipAttribute[qcount++]=poly.getClipLower().y

                clipAttribute[qcount++]=poly.getClipUpper().x
                clipAttribute[qcount++]=poly.getClipUpper().y
                clipAttribute[qcount++]=poly.getClipLower().x
                clipAttribute[qcount++]=poly.getClipLower().y

                indices[icount++] = (pcount * 3 + 0).toShort()
                indices[icount++] = (pcount * 3 + 1).toShort()
                indices[icount++] = (pcount * 3 + 2).toShort()

                val color1=path.getColor(0)
                val color2=path.getColor(1)
                val color3=path.getColor(2)

                colors[acount++] =color1.get(0)
                colors[acount++] =color1.get(1)
                colors[acount++] =color1.get(2)
                colors[acount++] =color1.get(3)

                colors[acount++] =color2.get(0)
                colors[acount++] =color2.get(1)
                colors[acount++] =color2.get(2)
                colors[acount++] =color2.get(3)

                colors[acount++] =color3.get(0)
                colors[acount++] =color3.get(1)
                colors[acount++] =color3.get(2)
                colors[acount++] =color3.get(3)
                pcount++
                num_triangles+=1
            }

        }
        mTexture=vertex.getTexture().getId()
        return vcount

    }

    private fun transform(){
        Matrix.setIdentityM(mModelMatrix,0)
        Matrix.translateM(mModelMatrix,0,position.x,position.y,position.z)
        Matrix.rotateM(mModelMatrix,0,rotation.z,0.0f,0.0f,1.0f)
        Matrix.scaleM(mModelMatrix,0,1.0f,1.0f,1.0f)
    }

    //initialize vertex byte buffer for shape coordinates
    private fun createVertexBuffer(){
        vertexBuffer=Buffer.createFloatBuffer(buffers[0],0,vertexes)
    }

    // initialize the drawList
    private fun initializeDrawList(){
        drawListBuffer=Buffer.createDrawListBuffer(0,indices)
    }

    private fun createTextOptionsBuffer(){
        textOptionsBuffer=Buffer.createFloatBuffer(buffers[7],0,textOptions)
    }
    private fun createColorBuffer(){
        colorBuffer=Buffer.createFloatBuffer(buffers[1],0,colors)
     }

    private fun createTextureBuffer(){
        textureBuffer=Buffer.createFloatBuffer(buffers[2],0,textures)
    }

    private fun createCenterBuffer(){
        centerBuffer=Buffer.createFloatBuffer(buffers[3],0,centerVertex)
    }

    private fun createRoundedPropertiesBuffer(){
        roundedPropBuffer=Buffer.createFloatBuffer(buffers[4],0,roundedRectProperties)
    }

    private fun createTrimBuffer(){
        clipBuffer=Buffer.createFloatBuffer(buffers[5],0,clipAttribute)
    }

    private fun createTransformBuffer(){
        transformBuffer=Buffer.createFloatBuffer(buffers[6],0,transforms)
    }

    // bind vertex shader attributes
    private fun bindVertexShader(){
        vertexBuffer?.put(vertexes)?.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[0])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,vcount*4,vertexBuffer)
        defaultShader.enableVertexAttribPointer("a_position",VERTEX_COORDS_PER_VERTEX,12,vertexBuffer)
        // pass in every circle or quads center position
        centerBuffer?.put(centerVertex)?.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[3])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,mcount*4,centerBuffer)
      //  if(primitiveType== Primitives.QUAD||primitiveType==Primitives.CIRCLE) {
            defaultShader.enableVertexAttribPointer("a_center",4,0,centerBuffer)
            // pass the rounded corners for rectF shape
            roundedPropBuffer?.put(roundedRectProperties)?.position(0)
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[4])
            GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,rcount*4,roundedPropBuffer)
            defaultShader.enableVertexAttribPointer("a_rounded_properties",2,0,roundedPropBuffer)
        //}
        clipBuffer?.put(clipAttribute)?.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[5])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,qcount*4,clipBuffer)
        defaultShader.enableVertexAttribPointer("a_trim",4,16,clipBuffer)
        //send transformation data
        val transFormStrideBytes=6*4
        val rotationOffset=0
        val scaleOffset=3
        transformBuffer?.clear()
        transformBuffer?.put(transforms)?.position(rotationOffset)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[6])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,ncount*4,transformBuffer)
        defaultShader.enableVertexAttribPointer("a_transform",3,transFormStrideBytes,transformBuffer)
        transformBuffer?.put(transforms)?.position(scaleOffset)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[6])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,ncount*4,transformBuffer)
        defaultShader.enableVertexAttribPointer("a_scale",3,transFormStrideBytes,transformBuffer)
        // send distance fields data
        val distanceFieldStrideBytes=8*4
        val boundsOffset=0
        val colorOffset=4
        if(isText) {
            textOptionsBuffer?.clear()
            textOptionsBuffer?.put(textOptions)?.position(boundsOffset)
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, buffers[7])
            GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER, 0, ocount * 4, textOptionsBuffer)
            defaultShader.enableVertexAttribPointer(
                "a_distanceFieldBounds",
                4,
                distanceFieldStrideBytes,
                textOptionsBuffer
            )
            textOptionsBuffer?.put(textOptions)?.position(colorOffset)
            GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, buffers[7])
            GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER, 0, ocount * 4, textOptionsBuffer)
            defaultShader.enableVertexAttribPointer(
                "a_distanceFieldColor",
                4,
                distanceFieldStrideBytes,
                textOptionsBuffer
            )
        }


    }

    // bind fragment shader attributes
    private fun bindFragmentShader(){
        //bind color
        colorBuffer?.put(colors)?.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[1])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,acount*4,colorBuffer)
        defaultShader.enableVertexAttribPointer("a_color",COLOR_COORDS_PER_VERTEX,0,colorBuffer)
        //bind texture
        val textureUniformHandle=defaultShader.getUniformLocation("u_texture")
        //this test if its a valid texture in the shader
        defaultShader.uniformLi("sampleId",mTexture)
        // pass texture coordinate info
        textureBuffer?.put(textures)?.position(0)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffers[2])
        GLES32.glBufferSubData(GLES32.GL_ARRAY_BUFFER,0,tcount*4,textureBuffer)
        defaultShader.enableVertexAttribPointer("a_TexCoordinate",2,0,textureBuffer)
        //set to unit 0
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D,mTexture)
        GLES32.glUniform1i(textureUniformHandle,0)
    }

    private fun render(){
        defaultShader.use()
        defaultShader.getUniformMatrix4fv("MVPMatrix",1,mMVPMatrix)
        defaultShader.uniform2f("srcRes",ScreenRatio.getInstance().getSurfaceScreen().x,ScreenRatio.getInstance().getSurfaceScreen().y)
        defaultShader.uniform1f("isQuad",if((primitiveType== Primitives.QUAD||primitiveType==Primitives.CIRCLE)&&!isText)1f else 0f)
        defaultShader.uniformLi("isText",if(isText)1 else 0)
        bindVertexShader()
        bindFragmentShader()
        if(primitiveType == Primitives.QUAD||primitiveType== Primitives.CIRCLE||primitiveType== Primitives.TRIANGLE)
             GLES32.glDrawElements(GLES32.GL_TRIANGLES,icount,GLES32.GL_UNSIGNED_SHORT,drawListBuffer)
        else
            if(primitiveType == Primitives.LINE||primitiveType== Primitives.POLYLINE){
             GLES32.glDrawElements(GLES32.GL_LINES,icount,GLES32.GL_UNSIGNED_SHORT,drawListBuffer)
        }
    }

    private fun draw() {
        drawListBuffer?.put(indices)
        drawListBuffer?.position(0)
        camera?.update(mViewMatrix)
        Matrix.setIdentityM(mModelMatrix,0)
        transform()
        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mModelMatrix,0)
        Matrix.multiplyMM(mMVPMatrix,0,camera?.getProjectionMatrix(),0,mMVPMatrix,0)
        render()
        num_draw_calls++


    }

    fun initShader(context: Context){
        defaultShader.createProgram(context)

    }
}