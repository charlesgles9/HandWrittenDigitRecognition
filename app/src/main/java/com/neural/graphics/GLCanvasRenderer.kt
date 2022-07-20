package com.neural.graphics

import android.app.Activity
import android.content.Context
import android.opengl.GLES32
import android.util.Log
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.BatchQueue
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Font
import com.graphics.glcanvas.engine.ui.*
import com.graphics.glcanvas.engine.utils.FpsCounter
import com.graphics.glcanvas.engine.utils.TextureLoader
import com.neural.graphics.ai.NeuralNetwork
import com.neural.graphics.constants.State
import com.neural.graphics.io.AIMetaData
import com.neural.graphics.io.FileUtility
import com.neural.graphics.mnistReader.MnistDataReader
import com.neural.graphics.mnistReader.MnistMatrix
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.system.exitProcess

class GLCanvasRenderer(private val context:Context,private val width:Float,private val height:Float):GLRendererView(width,height){
    private var batch=Batch()
    private var currentCycle=0
    private var targetCycle=500
    private var saveData=false
    private var savePending=false
    private val camera=Camera2D(10f)
    private val trainEntities= mutableListOf<MnistMatrix>()
    private val testEntities= mutableListOf<MnistMatrix>()
    private var state=State.LOADING
    private var mnistEntity=MnistEntity(MnistMatrix(28,28), Vector2f(),5f)
    private val neuralNetwork=NeuralNetwork(784,16,10)
    private val canvas=DrawCanvas(Vector2f(10f,470f),8f)
    private var train=true
    // main UI
    private val button=GLImageButton(100f,50f)
    private val font=Font("fonts/calibri.fnt",context)
    private val answerLabel=GLLabel(200f,50f,font,"0",0.3f)
    private var answer="value: 0"
    private val fpsLabel=GLLabel(100f,40f,font,"FPS: ",0.3f)
    private val savingDataLabel=GLLabel(150f,50f,font,"Saving...",0.2f)
    private val controlLayout=LinearLayoutConstraint(null,300f,80f)
    private val prev=GLLabel(100f,50f,font,"Prev",0.3f)
    private val next=GLLabel(100f,50f,font,"Next",0.3f)
    private val bgLayout=RelativeLayoutConstraint(null,width,height)
    //loading UI
    private val loadingLinearLayout=LinearLayoutConstraint(null,width*0.9f,200f)
    private val loadingBar=GLProgressBar(width*0.6f,40f,0f,true)
    //exit dialog
    private val exitLayout=RelativeLayoutConstraint(bgLayout,width*0.8f,200f)
    private var testIndex=0
    private var trainDataReader=MnistDataReader()
    private var testDataReader=MnistDataReader()
    private val path = "/NeuralGraphics/data.json"

    override fun prepare() {
        batch.initShader(context)
        camera.setOrtho(width, height)
        TextureLoader.getInstance().getTexture(context,"fonts/calibri.png")
        neuralNetwork.setDrawPosition(Vector2f(280f,350f))
        loadDataThread()
        val meta = AIMetaData(neuralNetwork)
        button.setBackgroundColor(ColorRGBA(0.2f,0.5f,0.5f,0.5f))
        button.setRippleColor(ColorRGBA(0.2f,0.6f,0.6f,0.8f))
        answerLabel.set(80f, 450f)
        savingDataLabel.set(540f,50f)
        button.setText("Clear",font,0.2f)
        button.setTextColor(ColorRGBA.green)


        button.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
               canvas.resetCanvas()
            }
        })

        next.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
                testIndex=(testIndex+1)%testEntities.size
                canvas.getEntity().initGrid(testEntities[testIndex])
            }
        })

        prev.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
                testIndex= max((testIndex-1),0)
                canvas.getEntity().initGrid(testEntities[testIndex])
            }
        })

        val infoLayout=LinearLayoutConstraint(null,300f,250f)
        infoLayout.setColor(ColorRGBA.transparent)
        val inputLabel=GLLabel(250f,40f,font,"Input Neurons: 784",0.2f)
            inputLabel.getConstraints().layoutMarginTop(20f)

        val hiddenLabel=GLLabel(250f,40f,font,"Hidden Neurons: 16",0.2f)
        val outputLabel=GLLabel(250f,40f,font,"Output Neurons: 10",0.2f)

        val learningRateLabel=GLLabel(150f,50f,font,"Learning Rate",0.2f)
        val learningRateDropDown=GLDropDown(130f,50f,font,"0.01",0.18f)
            learningRateDropDown.setBackgroundColor(ColorRGBA(0.2f,0.4f,0.4f,0.4f))
            learningRateDropDown.setRippleColor(ColorRGBA(0.2f,0.4f,0.4f,0.4f))
            learningRateDropDown.setDropMaxHeight(300f)
            learningRateDropDown.setDropDownRounded(15f)
            learningRateDropDown.setItems(mutableListOf("1.0","0.5","0.1","0.05","0.01","0.005","0.001","0.0005","0.0001"))
            learningRateDropDown.setDropDownBackgroundColor(ColorRGBA(0.2f,0.5f,0.5f,0.5f))
            learningRateDropDown.getConstraints().toRightOf(learningRateLabel)
            learningRateDropDown.getConstraints().alignCenterVertical(learningRateLabel)
            infoLayout.getConstraints().alignBelow(fpsLabel)

        learningRateDropDown.setOnItemClickListener(object :OnItemClickEvent.OnItemClickListener{
            override fun onItemClick(view: GLView) {
                  val lr=view.getTextView()?.getText()?:"0.01"
                  neuralNetwork.setLearningRate(lr.toDouble())
            }
        })
        infoLayout.setOrientation(LinearLayoutConstraint.VERTICAL)
        infoLayout.addItem(inputLabel)
        infoLayout.addItem(hiddenLabel)
        infoLayout.addItem(outputLabel)
        infoLayout.addItem(learningRateLabel)
        infoLayout.addItem(learningRateDropDown)
        learningRateDropDown.addEvents(getController())
        fpsLabel.getConstraints().layoutMarginLeft(5f)
        fpsLabel.getConstraints().alignCenterHorizontal(bgLayout)
        infoLayout.getConstraints().alignEnd(bgLayout)
        savingDataLabel.getConstraints().alignEnd(bgLayout)
        bgLayout.setPosition(0f,0f)
        bgLayout.setBackgroundColor(ColorRGBA(0.4f,0.4f,0.4f,0.5f))
        bgLayout.roundedCorner(10f)
        bgLayout.addItem(fpsLabel)
        bgLayout.addItem(savingDataLabel)
        bgLayout.addItem(infoLayout)
        next.setRippleColor(ColorRGBA(0.2f,0.4f,0.4f,0.5f))
        prev.setRippleColor(ColorRGBA(0.2f,0.4f,0.4f,0.5f))

        val toggleLayout=LinearLayoutConstraint(null,190f,80f)
            toggleLayout.setOrientation(LinearLayoutConstraint.HORIZONTAL)
            toggleLayout.setBackgroundColor(ColorRGBA.transparent)
            toggleLayout.getConstraints().layoutMarginLeft(10f)
        val toggleTitle=GLLabel(150f,50f,font,"Training Enabled ",0.18f)
        val toggle=GLCheckBox(30f,30f, ColorRGBA.blue)
        toggle.setCheckedColor(ColorRGBA.green)
        toggle.roundedCorner(10f)
        toggle.setChecked(true)
        toggleLayout.getConstraints().alignCenterVertical(bgLayout)

        toggle.setOnClickListener(object: OnClickEvent.OnClickListener{
            override fun onClick() {
             train=!toggle.getChecked()
            }
        })
        toggleLayout.addItem(toggleTitle)
        toggleLayout.addItem(toggle)
        bgLayout.addItem(toggleLayout)

        val resetNetwork=GLLabel(70f,40f,font,"Reset",0.18f)
        resetNetwork.getConstraints().alignEnd(bgLayout)
        resetNetwork.getConstraints().alignBelow(savingDataLabel)
        resetNetwork.setBackgroundColor(ColorRGBA(0.4f,0f,0f,0.5f))
        resetNetwork.setRippleColor(ColorRGBA(0.7f,0f,0f,0.7f))
        resetNetwork.roundedCorner(20f)
        resetNetwork.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
              meta.resetNetwork(context,path)
            }
        })
        bgLayout.addItem(resetNetwork)
        getController()?.addEvent(toggle)
        getController()?.addEvent(button)
        getController()?.addEvent(next)
        getController()?.addEvent(prev)
        getController()?.addEvent(resetNetwork)
        controlLayout.setBackgroundColor(ColorRGBA.transparent)
        controlLayout.setOrientation(LinearLayoutConstraint.HORIZONTAL)
        controlLayout.addItem(prev)
        controlLayout.addItem(button)
        controlLayout.addItem(next)
        controlLayout.set(140f,750f)


        // setup the loading UI
        loadingLinearLayout.setOrientation(LinearLayoutConstraint.VERTICAL)
        loadingLinearLayout.set(width*0.5f,height*0.5f)
        loadingLinearLayout.setColor(ColorRGBA(0.2f,0.4f,0.4f,0.5f))
        val loadingTitle=GLLabel(350f,80f,font,"Loading Data...",0.4f)
        loadingTitle.getConstraints().alignCenterHorizontal(loadingLinearLayout)
        loadingBar.setBackgroundColor(ColorRGBA(0.1f,0.5f,0.2f,0.5f))
        loadingBar.setForegroundColor(ColorRGBA.green)
        loadingBar.roundedCorner(15f)
        loadingBar.getConstraints().alignCenterHorizontal(loadingLinearLayout)
        loadingBar.setText("0%",font,0.3f)
        loadingLinearLayout.addItem(loadingTitle)
        loadingLinearLayout.addItem(loadingBar)
        loadingLinearLayout.roundedCorner(10f)
        //exit dialog
        val exitButton=GLImageButton(100f,40f)
        val stayButton=GLImageButton(100f,40f)
        val exitTitle=GLLabel(250f,50f,font,"Exit Program?",0.35f)
        exitButton.roundedCorner(10f)
        stayButton.roundedCorner(10f)
        exitButton.getConstraints().alignCenterVertical(exitLayout)
        stayButton.getConstraints().alignCenterVertical(exitLayout)
        stayButton.getConstraints().alignEnd(exitLayout)
        exitTitle.getConstraints().alignCenterHorizontal(exitLayout)
        stayButton.setText("Stay",font,0.15f)
        exitButton.setText("Exit",font,0.15f)
        stayButton.getConstraints().layoutMarginRight(10f)
        exitButton.getConstraints().layoutMarginLeft(10f)
        exitButton.setBackgroundColor(ColorRGBA(0.2f,0.5f,0.4f,1f))
        stayButton.setBackgroundColor(ColorRGBA(0.2f,0.5f,0.4f,1f))
        exitButton.setRippleColor(ColorRGBA(0.2f,0.8f,0.4f,1f))
        stayButton.setRippleColor(ColorRGBA(0.2f,0.8f,0.4f,1f))

        exitLayout.getConstraints().alignCenter(bgLayout)
        exitLayout.setEnableTouchEvents(false)
        exitLayout.roundedCorner(20f)
        exitLayout.setBackgroundColor(ColorRGBA(0.5f,0.2f,0.2f,1f))
        exitLayout.addItem(exitButton)
        exitLayout.addItem(stayButton)
        exitLayout.addItem(exitTitle)
        exitLayout.setZ(bgLayout.getZ()+1f)
        exitLayout.setVisibility(false)
        stayButton.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
                exitLayout.setEnableTouchEvents(false)
                bgLayout.setEnableTouchEvents(true)
                exitLayout.setVisibility(false)
                canvas.enableTouchEvents(true)
            }
        })

        exitButton.setOnClickListener(object :OnClickEvent.OnClickListener{
            override fun onClick() {
                exitProcess(0)
            }
        })

         getController()?.addEvent(stayButton)
         getController()?.addEvent(exitButton)


        if(!FileUtility.checkStoragePermissionDenied(context as Activity)) {
            if (meta.saveDataExists(path, context)) {
                meta.loadSaveData(context, path)
            }else{
                meta.populateDataFromAssets(context)
                meta.saveData(context, path)
            }
            // in case the user denies this application permission
        }else
            meta.populateDataFromAssets(context)

    }

   var iterations=0
   var max=44999

    private fun loadDataThread(){
        thread {
            kotlin.run {
                val trainMatrices= trainDataReader.readData(context.assets.open("data/train-images.idx3-ubyte"),
                    context.assets.open("data/train-labels.idx1-ubyte"))
                val testMatrices=testDataReader.readData(context.assets.open("data/t10k-images.idx3-ubyte"),
                    context.assets.open("data/t10k-labels.idx1-ubyte"))
                val trainSize=45000
                val testSize=9000
                for(i in 0 until trainSize)
                    trainEntities.add(trainMatrices[i])
                for(i in 0 until testSize)
                    testEntities.add(testMatrices[i])
                mnistEntity= MnistEntity(trainMatrices[0],Vector2f(10f,10f),8f)
                getController()?.addEvent(canvas.getEvent()!!)
                createTrainingThread()
                state=State.RUNNING
            }
        }
    }

    private fun createTrainingThread(){
        thread {
            kotlin.run {
                while (true){

                    trainNetwork()
                }
            }
        }
    }

    private fun createSaveThread(){
        if(!FileUtility.checkStoragePermissionDenied(context as Activity)) {
            savePending = true
            thread {
                kotlin.run {
                    AIMetaData(neuralNetwork).saveData(context, "/NeuralGraphics/data.json")
                    savePending = false
                    saveData = false
                }
            }
        }
    }
    override fun draw() {
       GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
       GLES32.glClearColor(0f,0f,0f,1f)


        when(state){
            State.RUNNING->{
                batch.setMode(BatchQueue.UNORDER)
                batch.begin(camera)
                bgLayout.draw(batch)
                exitLayout.draw(batch)
                mnistEntity.draw(batch)
                canvas.draw(batch)
                answerLabel.draw(batch)
                controlLayout.draw(batch)
                neuralNetwork.draw(batch)
                batch.end()
            }
            State.LOADING->{
                batch.setMode(BatchQueue.UNORDER)
                batch.begin(camera)
                loadingLinearLayout.draw(batch)
                loadingBar.setProgress((trainDataReader.progress+testDataReader.progress)*0.5f)
                loadingBar.getTextView()?.setText(""+loadingBar.getProgress().toInt()+"%")
                batch.end()
            }
        }


    }

    private fun trainNetwork(){
        val testInput= mutableListOf<Double>()
        if(train) {
            val trainInput = mutableListOf<Double>()
            mnistEntity.initGrid(trainEntities[iterations])
            for (i in 0 until mnistEntity.size())
                trainInput.add(mnistEntity.getGridValue(i))
            neuralNetwork.train(trainInput,mnistEntity.getExpectedOutput())
            iterations=(iterations+1)%max
            if(currentCycle>targetCycle){
                saveData=true
                targetCycle=(targetCycle+ 500)%2000
                if(targetCycle==0) {
                    targetCycle = 500
                    currentCycle=0
                }
            }
            if(saveData&&!savePending)
                createSaveThread()

            currentCycle++
        }
        for (i in 0 until canvas.getEntity().size())
            testInput.add(canvas.getEntity().getGridValue(i))
        val outputs = neuralNetwork.predict(testInput)
        neuralNetwork.setDrawOutputs(outputs)
        var largest=0.0
        var pick=0
        for (i in 0 until outputs.size) {
            if(largest<=outputs[i]) {
                pick = i
                largest=outputs[i]
            }
        }
         answer="Value: $pick"
    }

    override fun update(delta: Long) {
       fpsLabel.setText("FPS: "+FpsCounter.getInstance().getFps())
        savingDataLabel.setText(if(saveData)"saving.." else "")
       answerLabel.setText(answer)
    }


    fun exit(){
     exitLayout.setVisibility(true)
     exitLayout.setEnableTouchEvents(true)
     bgLayout.setEnableTouchEvents(false)
     canvas.enableTouchEvents(false)
    }
}