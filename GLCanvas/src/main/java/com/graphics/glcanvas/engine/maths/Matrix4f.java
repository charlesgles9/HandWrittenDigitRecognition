package com.graphics.glcanvas.engine.maths;

public class Matrix4f {
    private float [][]array;

    public Matrix4f(){
        array= new float[4][4];
    }
    public Matrix4f(float entries){
        this();
        this.array[0][0]=entries;
        this.array[1][1]=entries;
        this.array[2][2]=entries;
        this.array[3][3]=0;
    }

    public Matrix4f(Vector3f vector){
        this();
        this.array[0][0]=vector.getX();
        this.array[1][1]=vector.getY();
        this.array[2][2]=vector.getZ();
    }
    public Matrix4f(float x,float y,float z){
        this();
        this.array[0][0]=x;
        this.array[1][1]=y;
        this.array[2][2]=z;
    }
    public static Vector3f Multiply(Matrix4f matrix4f,Vector3f vector){
           float x=matrix4f.array[0][0]*vector.getX()*matrix4f.array[1][0]*vector.getY()+
                   matrix4f.array[2][0]*vector.getZ();
           float y=matrix4f.array[0][1]*vector.getX()+matrix4f.array[1][1]*vector.getY()+
                   matrix4f.array[2][1]*vector.getZ();
           float z=matrix4f.array[0][2]*vector.getX()+matrix4f.array[1][2]*vector.getY()+
                   matrix4f.array[2][2]*vector.getZ();
           return new Vector3f(x,y,z);
    }
    public static Vector3f Multiply(Matrix4f matrix4f,float vx,float vy,float vz){
        float x=matrix4f.array[0][0]*vx*matrix4f.array[1][0]*vy+
                matrix4f.array[2][0]*vz;
        float y=matrix4f.array[0][1]*vx+matrix4f.array[1][1]*vy+
                matrix4f.array[2][1]*vz;
        float z=matrix4f.array[0][2]*vx+matrix4f.array[1][2]*vy+
                matrix4f.array[2][2]*vz;
        return new Vector3f(x,y,z);
    }
   public void setRotationZ(float angle){
     angle=(float)Math.toRadians(angle);
     this.array[0][0]=(float)Math.cos(angle);
     this.array[0][1]=(float)-Math.sin(angle);
     this.array[0][2]=0f;
     this.array[1][0]=(float)Math.sin(angle);
     this.array[1][1]=(float)Math.cos(angle);
     this.array[1][2]=0f;
     this.array[2][0]=0f;
     this.array[2][1]=0f;
     this.array[2][2]=1f;
   }

   public void setRotationX(float angle){
        this.array[0][0]=1f;
        this.array[0][1]=0f;
        this.array[0][2]=0f;
        this.array[1][0]=0f;
        this.array[1][1]=(float)Math.cos(angle);
        this.array[1][2]=(float)-Math.sin(angle);
        this.array[2][0]=0f;
        this.array[2][1]=(float)Math.sin(angle);
        this.array[2][2]=(float)Math.cos(angle);
   }

   public void setRotationY(float angle){
        this.array[0][0]=(float)Math.cos(angle);
        this.array[0][1]=0f;
        this.array[0][2]=-(float)Math.sin(angle);
        this.array[1][0]=0f;
        this.array[1][1]=1f;
        this.array[1][2]=0f;
        this.array[2][0]=(float)Math.sin(angle);
        this.array[2][1]=0f;
        this.array[2][2]=(float)Math.cos(angle);
   }

}
