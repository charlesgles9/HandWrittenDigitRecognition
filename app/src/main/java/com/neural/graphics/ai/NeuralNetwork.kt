package com.neural.graphics.ai

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line
import com.neural.graphics.algebra.Matrix
import kotlin.math.abs

class NeuralNetwork(  inputCount:Int, hiddenCount:Int,  outputCount:Int) {

  private val weightsInputHidden=Matrix(hiddenCount,inputCount)
  private val weightsHiddenOutput=Matrix(outputCount,hiddenCount)
  private val biasHidden=Matrix(hiddenCount,1)
  private val biasOutput=Matrix(outputCount,1)
  private val position=Vector2f(0f,0f)
  private val neuronsInput= List(10,init = {Circle(0f,0f,12f)})
  private val neuronsHidden=List(16,init = {Circle(0f,0f,10f)})
  private val neuronsOutput=List(10,init = {Circle(0f,0f,12f)})
  private val connectionsInputHidden=List(neuronsInput.size*neuronsHidden.size,init = {Line(0f,0f,0f,0f)})
  private val connectionsHiddenOutput=List(neuronsInput.size*neuronsHidden.size,init = {Line(0f,0f,0f,0f)})
  private var LEARNING_RATE=0.01
  private var drawOutputs= MutableList(10,init = {0.0})

    fun setDrawPosition(position:Vector2f){
        this.position.set(position)
        for(i in neuronsInput.indices){
            val neuron=neuronsInput[i]
            neuron.set(position.x,position.y+i*neuron.getRadius()*2f+15f)
        }

         val centerOffset= abs(neuronsInput.size*12f-neuronsHidden.size*10f)*0.5f
        for(i in neuronsHidden.indices){
            val neuron=neuronsHidden[i]
            neuron.set(position.x+120f,position.y-centerOffset+i*(neuron.getRadius()*2f))
        }
        for(i in neuronsOutput.indices){
            val neuron=neuronsOutput[i]
            neuron.set(position.x+220f,position.y+i*neuron.getRadius()*2f+15f)
        }
        for(i in neuronsInput.indices){
            for(j in neuronsHidden.indices){
                val index=j*neuronsInput.size+i
                val lineInputHidden=connectionsInputHidden[index]
                val lineHiddenOutput=connectionsHiddenOutput[index]
                val startInput=neuronsInput[i]
                val stopHidden= neuronsHidden[j]
                val stopOutput= neuronsOutput[i]
                stopHidden.setColor(ColorRGBA.green)
                lineInputHidden.set(startInput.getX(),startInput.getY(),stopHidden.getX(),stopHidden.getY())
                lineHiddenOutput.set(stopHidden.getX(),stopHidden.getY(),stopOutput.getX(),stopOutput.getY())
            }
        }
    }

    fun getLearningRate():Double{
        return LEARNING_RATE
    }

    fun setLearningRate(value:Double){
        LEARNING_RATE=value
    }

    fun getWeightsInputHidden():Matrix{
        return weightsInputHidden
    }

    fun getWeightsHiddenOutput():Matrix{
        return weightsHiddenOutput
    }

    fun getBiasHidden():Matrix{
        return biasHidden
    }

    fun getBiasOutput():Matrix{
        return biasOutput
    }
    //forward propagation
  fun predict(values:MutableList<Double>):MutableList<Double>{
      val input=Matrix.fromArray(values)
      val hidden=Matrix.multiply(weightsInputHidden,input)
      hidden.add(biasHidden)
      hidden.sigmoid()

      val output=Matrix.multiply(weightsHiddenOutput,hidden)
      output.add(biasOutput)
      output.sigmoid()
      return output.toArray()
  }


    fun train(inputValues:MutableList<Double>,expectedValues:MutableList<Double>){
        val input=Matrix.fromArray(inputValues)
        val hidden=Matrix.multiply(weightsInputHidden,input)
        hidden.add(biasHidden)
        hidden.sigmoid()

        val output=Matrix.multiply(weightsHiddenOutput,hidden)
        output.add(biasOutput)
        output.sigmoid()

        val target=Matrix.fromArray(expectedValues)

        val error=Matrix.subtract(target,output)
        val gradient=output.dSigmoid()
        gradient.multiply(error)
        gradient.multiply(LEARNING_RATE)

        val hiddenTranspose=Matrix.transpose(hidden)
        val weightedHiddenOutputDelta=Matrix.multiply(gradient,hiddenTranspose)

        weightsHiddenOutput.add(weightedHiddenOutputDelta)
        biasOutput.add(gradient)

        val weightedHiddenOutputTranspose=Matrix.transpose(weightsHiddenOutput)
        val hiddenErrors=Matrix.multiply(weightedHiddenOutputTranspose,error)

        val hiddenGradient=hidden.dSigmoid()

        hiddenGradient.multiply(hiddenErrors)
        hiddenGradient.multiply(LEARNING_RATE)

        val inputTranspose=Matrix.transpose(input)
        val weightedInputHiddenDelta=Matrix.multiply(hiddenGradient,inputTranspose)
        weightsInputHidden.add(weightedInputHiddenDelta)
        biasHidden.add(hiddenGradient)
    }

    fun fit(inputValues:MutableList<Double>,expectedValues:MutableList<Double>){
        train(inputValues, expectedValues)
    }


    fun setDrawOutputs(output:MutableList<Double>){
        for(i in 0 until  output.size){
            drawOutputs[i] = output[i]
        }
    }

     fun draw(batch: Batch) {
        connectionsInputHidden.forEach { connection->batch.draw(connection) }
        connectionsHiddenOutput.forEach { connection->batch.draw(connection) }
        neuronsInput.forEach { neuron-> batch.draw(neuron) }
        neuronsHidden.forEach { neuron-> batch.draw(neuron) }
        for(i in 0 until drawOutputs.size) {
            val neuron=neuronsOutput[i]
                val v=(0.1+drawOutputs[i]).toFloat()
                neuron.setColor(ColorRGBA(v+0.4f,v+0.4f,v+0.4f,1f))
            batch.draw(neuron)
        }

    }
}