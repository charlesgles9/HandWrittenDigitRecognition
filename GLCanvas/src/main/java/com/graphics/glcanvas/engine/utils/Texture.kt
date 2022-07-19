package com.graphics.glcanvas.engine.utils

import android.content.Context

class Texture (){
    private var mTexture=0

    constructor(context: Context,path: String) : this() {
        load(context, path)
    }

    constructor(path:String) : this() {
        preLoaded(path)
    }


    fun load(context: Context,path:String){
        mTexture=TextureLoader.getInstance().getTexture(context,path)
    }

    fun preLoaded(path:String){
        this.mTexture=TextureLoader.getInstance().getTexture(path)
    }

    fun setId(mTexture:Int){
        this.mTexture=mTexture
    }

    fun getId():Int{
        return mTexture
    }

}