package com.neural.graphics.algebra

import java.lang.ArithmeticException
import kotlin.math.exp
import kotlin.random.Random

class Matrix(val rows:Int,  val cols:Int) {

     var data=Array(rows,init = {DoubleArray(cols)})
    init {
        for(i in data.indices){
            for(j in data[i].indices){
                data[i][j]=Random.nextDouble()*2.0-1.0
            }
        }
    }


    fun reset(){
        for(i in data.indices){
            for(j in data[i].indices){
                data[i][j]=Random.nextDouble()*2.0-1.0
            }
        }
    }

      fun copy(src:Array<Double>){
          for(i in data.indices){
              for(j in data[i].indices){
                  data[i][j]=src[i*cols+j]
              }
          }
      }

      fun  add(matrix:Matrix){
        if(rows!=matrix.rows||cols!=matrix.cols)
            throw ArithmeticException("Invalid matrix addition")
         for (i in 0 until rows){
             for(j in 0 until cols){
                 data[i][j]+=matrix.data[i][j]
             }
         }
   }
    companion object {
        fun subtract(firstMat: Matrix, secondMat: Matrix):Matrix {
            val temp=Matrix(firstMat.rows,firstMat.cols)
            for (i in 0 until firstMat.rows){
                for(j in 0 until firstMat.cols){
                    temp.data[i][j]=firstMat.data[i][j]-secondMat.data[i][j]
                }
            }
            return temp
        }
        fun transpose(matrix: Matrix):Matrix{
            val temp=Matrix(matrix.cols,matrix.rows)
            for (i in 0 until matrix.rows){
                for(j in 0 until matrix.cols){
                    temp.data[j][i]=matrix.data[i][j]
                }
            }
            return temp
        }
       fun multiply(firstMat: Matrix,secondMat: Matrix):Matrix{
           val temp=Matrix(firstMat.rows,secondMat.cols)
           for (i in 0 until temp.rows){
               for(j in 0 until temp.cols){
                   var sum=0.0
                   for(k in 0 until firstMat.cols){
                       sum+=firstMat.data[i][k]*secondMat.data[k][j]
                   }
                   temp.data[i][j]=sum
               }
           }
           return temp
       }

        fun fromArray(array:MutableList<Double>):Matrix{
            val temp=Matrix(array.size,1)
            for(i in array.indices)
                temp.data[i][0]=array[i]
            return temp
        }

      }
    fun multiply(matrix: Matrix) {
        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.cols) {
                data[i][j] *= matrix.data[i][j]
            }
        }
    }
    fun multiply(scalar:Double) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] *= scalar
            }
        }
    }

    // activation function
    fun sigmoid(){
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                data[i][j] =1.0/(1.0+ exp(-data[i][j]))
            }
        }
    }
    fun dSigmoid():Matrix{
        val temp=Matrix(rows ,cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                temp.data[i][j] =data[i][j]*(1.0-data[i][j])
            }
        }
        return temp
    }

    fun print(){
        for (i in 0 until rows) {
            for (j in 0 until cols) {
               print(data[i][j].toFloat() )
            }
            println()
        }
    }

   fun toArray():MutableList<Double> {
       val temp = mutableListOf<Double>()
       for (i in 0 until rows) {
           for (j in 0 until cols) {
               temp.add(data[i][j])
           }
       }
       return temp
   }
}