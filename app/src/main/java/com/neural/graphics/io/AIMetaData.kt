package com.neural.graphics.io

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.graphics.glcanvas.engine.utils.ResourceLoader
import com.neural.graphics.ai.NeuralNetwork
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

import kotlin.collections.HashMap

class AIMetaData(private val network: NeuralNetwork) {



    fun saveDataExists(path: String,context: Context):Boolean{
        val storage=context.getExternalFilesDirs(null)[0]
        val file=File(storage,path)
        return file.exists()
    }

    fun saveData(context: Context,path:String){
        val storage=context.getExternalFilesDirs(null)[0]
        val file=File(storage,path)
        val parent=file.parentFile
        if(parent?.exists()==false)
            parent.mkdir()
        if(!file.exists())
            file.createNewFile()
        val map=HashMap<Any,Any>()
        map["LEARNING_RATE"] = network.getLearningRate().toString()
        map["WeightsIH"]=network.getWeightsInputHidden().toArray()
      //  map["WeightsHO"]=network.getWeightsHiddenOutput().toArray()
       // weights["BiasHidden"]=network.getBiasHidden().toArray()
        //weights["BiasOutput"]=network.getBiasOutput().toArray()
         val jsonObject=JSONObject()
         jsonObject.put("LEARNING_RATE",network.getLearningRate().toString())
         jsonObject.put("WeightsIH",network.getWeightsInputHidden().toArray())
         jsonObject.put("WeightsHO",network.getWeightsHiddenOutput().toArray())

         val output=JSONArray().put(jsonObject)
         writeJson(file,output)
    }

    fun loadSaveData(context: Context,path: String){
        try {
          val jArray = loadJson(path, context)
        if(jArray!=null)
           populateDataToNetwork(jArray)
          else{
             populateDataFromAssets(context)
          }
        }catch (e:JSONException){ }
    }

    fun populateDataFromAssets(context: Context){
        val text=ResourceLoader().loadTextFromAssets(context,"data/data.json")
        populateDataToNetwork(JSONArray(text))
    }

    fun resetNetwork(context: Context,path: String){
        network.getWeightsHiddenOutput().reset()
        network.getWeightsInputHidden().reset()
        saveData(context,path)
    }

    private fun populateDataToNetwork(jArray: JSONArray){
        val jObject=jArray.getJSONObject(0)
        val inputHidden=jObject.getString("WeightsIH")
        val hiddenOutput=jObject.getString("WeightsHO")
        val rate=jObject.getString("LEARNING_RATE").toDouble()
        network.setLearningRate(rate)
        network.getWeightsInputHidden().copy(splitString(inputHidden.substring(1,inputHidden.length-1)))
        network.getWeightsHiddenOutput().copy( splitString(hiddenOutput.substring(1,hiddenOutput.length-1)))
    }

    private fun splitString(string: String):Array<Double>{
        val array=string.split(",")
        val list= Array(array.size,init = {0.0})
        for (i in array.indices)
            list[i]=array[i].trim().toDouble()
        return list
    }
    private fun writeJson(file:File,jsonArray: JSONArray){
        val writer=BufferedWriter(FileWriter(file))
            writer.write(jsonArray.toString(1))
            writer.flush()
    }

   private fun loadJson(path:String,context: Context):JSONArray?{
       val storage=context.getExternalFilesDirs(null)[0]
       val file=File(storage,path)
       if(!file.exists())
           return null
       val text=StringBuffer()
       val br=BufferedReader(FileReader(file))
       var line:String?=""
        while ((br.readLine().also {
                line = it })!=null){
            text.append(line)
          //  text.append('\n')
        }
       br.close()
       return JSONArray(text.toString())
   }

}