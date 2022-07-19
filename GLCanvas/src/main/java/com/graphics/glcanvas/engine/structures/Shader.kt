package com.graphics.glcanvas.engine.structures

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.utils.ResourceLoader
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Shader(private val vname:String,private val fname:String) {

    private var program=-1
    private var vertexShader:String=""
    private var fragmentShader:String=""
    private fun createShader(string:String,type:Int):Int {
        // load the shader
        val handle = GLES32.glCreateShader(type)
        if (handle != 0) {
            //pass shader source
            GLES32.glShaderSource(handle, string)
            //compile shader
            GLES32.glCompileShader(handle)
            // get compilation status
            val status = IntArray(1)
            GLES32.glGetShaderiv(handle, GLES32.GL_COMPILE_STATUS, status, 0)
            //compilation failed
            if (status[0] == 0) {
                val log=GLES32.glGetShaderInfoLog(handle)
                GLES32.glDeleteShader(handle)
                throw RuntimeException(if (type == GLES32.GL_VERTEX_SHADER) "Error creating vertex Shader$log" else "Error creating fragment shader$log")
            }
        }
        return handle
    }

    fun createProgram(context: Context){
       vertexShader= ResourceLoader().loadTextFromAssets(context,vname)
       fragmentShader= ResourceLoader().loadTextFromAssets(context,fname)
        program=GLES32.glCreateProgram()
        if(program!=0){

            // bind vertex shader
            GLES32.glAttachShader(program,createShader(vertexShader,GLES32.GL_VERTEX_SHADER))
            // bind fragment shader
            GLES32.glAttachShader(program,createShader(fragmentShader,GLES32.GL_FRAGMENT_SHADER))
            // bind attributes
            GLES32.glBindAttribLocation(program,0,"a_position")
            GLES32.glBindAttribLocation(program,1,"a_color")
            GLES32.glLinkProgram(program)
            //link status
            val status=IntArray(1)
            GLES32.glGetProgramiv(program,GLES32.GL_LINK_STATUS,status,0)
            if(status[0]==0){
                GLES32.glDeleteProgram(program)
                throw RuntimeException("Error creating program")
            }

        }
    }

    fun use(){
        GLES32.glUseProgram(getProgram())
    }

     fun getUniformLocation(name:String):Int{
        return GLES32.glGetUniformLocation(getProgram(),name)
    }

    fun getUniformMatrix4fv(name:String,count:Int,mat:FloatArray){
        GLES32.glUniformMatrix4fv(getUniformLocation(name),count,false,mat,0)
    }

    fun uniform2f(name:String,x:Float,y:Float){
        GLES32.glUniform2f(getUniformLocation(name),x,y)
    }

    fun uniformLi(name:String, value:Int){
        GLES32.glUniform1i(getUniformLocation(name),value)
    }

    fun uniform1f(name:String,value:Float){
        GLES32.glUniform1f(getUniformLocation(name),value)
    }

    fun uniform3f(name: String,x:Float,y:Float,z:Float){
        GLES32.glUniform3f(getUniformLocation(name),x,y,z)
    }
    fun uniform4f(name: String,x1:Float,y1:Float,x2:Float,y2:Float){
        GLES32.glUniform4f(getUniformLocation(name),x1,y1,x2,y2)

    }

    fun enableVertexAttribPointer(name:String,coords_per_vertex:Int,stride:Int,buffer:FloatBuffer?){
        val handle=GLES32.glGetAttribLocation(getProgram(),name)
        GLES32.glEnableVertexAttribArray(handle)
        //prepare triangle coordinate data
        GLES32.glVertexAttribPointer(
            handle,
            coords_per_vertex,
            GLES32.GL_FLOAT,
            false,
            stride,
            buffer)

    }
    fun enableVertexAttribPointer(name:String,coords_per_vertex:Int,stride:Int,buffer:IntBuffer?){
        val handle=GLES32.glGetAttribLocation(getProgram(),name)
        GLES32.glEnableVertexAttribArray(handle)
        //prepare triangle coordinate data
        GLES32.glVertexAttribPointer(
            handle,
            coords_per_vertex,
            GLES32.GL_INT,
            false,
            stride,
            buffer)

    }
    fun enableVertexAttribPointer(name:String,coords_per_vertex:Int,stride:Int,buffer:Int){
        val handle=GLES32.glGetAttribLocation(getProgram(),name)
        GLES32.glEnableVertexAttribArray(handle)
        //prepare triangle coordinate data
        GLES32.glVertexAttribPointer(
            handle,
            coords_per_vertex,
            GLES32.GL_FLOAT,
            false,
            stride,
            buffer)
    }


    fun disableVertexAttribPointer(name:String){
        GLES32.glDisableVertexAttribArray(GLES32.glGetAttribLocation(getProgram(),name))
    }
    fun getProgram():Int{
        return program
    }

}