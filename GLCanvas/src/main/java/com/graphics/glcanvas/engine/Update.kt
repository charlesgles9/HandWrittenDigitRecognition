package com.graphics.glcanvas.engine

interface Update {
    fun draw(batch: Batch)
    fun update(delta:Long)
}