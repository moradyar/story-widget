package com.moradyar.anroid.player.listeners

import com.moradyar.anroid.player.common.Progress

interface ProgressListener {
    fun onProgressChanged(progress: Progress)
}