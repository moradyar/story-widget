package com.moradyar.anroid.player.widget

import android.view.SurfaceHolder

open class SurfaceCreatedCallback(private val onSurfaceCreated: (SurfaceHolder) -> Unit) :
    SurfaceHolder.Callback {

    override fun surfaceCreated(holder: SurfaceHolder) {
        onSurfaceCreated(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }
}