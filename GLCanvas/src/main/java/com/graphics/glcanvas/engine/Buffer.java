package com.graphics.glcanvas.engine;

import android.opengl.GLES32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Buffer {

    static FloatBuffer createFloatBuffer(int buffer,int position,float[]data){
        int BYTES_PER_FLOAT = 4;
        ByteBuffer bb=ByteBuffer.allocateDirect(data.length* BYTES_PER_FLOAT);
        FloatBuffer floatBuffer=bb.order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(data);
        floatBuffer.position(position);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffer);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,floatBuffer.capacity()* BYTES_PER_FLOAT,null,GLES32.GL_DYNAMIC_DRAW);
        return floatBuffer;
    }
    static IntBuffer createIntBuffer(int buffer,int position,int[]data){
        int BYTES_PER_INT = 4;
        ByteBuffer bb=ByteBuffer.allocateDirect(data.length* BYTES_PER_INT);
        IntBuffer intBuffer=bb.order(ByteOrder.nativeOrder()).asIntBuffer();
        intBuffer.put(data);
        intBuffer.position(position);
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER,buffer);
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER,intBuffer.capacity()* BYTES_PER_INT,null,GLES32.GL_DYNAMIC_DRAW);
        return intBuffer;
    }

    static ShortBuffer createDrawListBuffer( int position, short[]data){
        int BYTES_PER_SHORT = 2;
        ByteBuffer dlb=ByteBuffer.allocateDirect(data.length* BYTES_PER_SHORT);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer drawListBuffer=dlb.asShortBuffer();
        drawListBuffer.put(data);
        drawListBuffer.position(position);
        return drawListBuffer;
    }
}
