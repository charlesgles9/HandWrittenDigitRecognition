package com.neural.graphics
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.graphics.glcanvas.engine.GLCanvasSurfaceView
import com.neural.graphics.io.FileUtility


class MainActivity : AppCompatActivity() {
    var renderer:GLCanvasRenderer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         renderer=GLCanvasRenderer(this, 600f, 800f)

        val surface=GLCanvasSurfaceView(this, renderer!!)
        supportActionBar?.hide()
        setContentView(surface)
        // check for storage permission
         FileUtility.grantStoragePermission(this)

    }


    override fun onBackPressed() {
        renderer?.exit()
    }
}