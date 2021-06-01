package com.moradyar.anroid.player.player.common

import com.google.android.exoplayer2.ui.PlayerView
import com.moradyar.anroid.player.listeners.ErrorListener
import com.moradyar.anroid.player.listeners.ProgressListener
import com.moradyar.anroid.player.listeners.SeekListener
import com.moradyar.anroid.player.listeners.StateListener

interface Player {

    fun pause()

    fun stop()

    fun resume()

    fun playPause()

    fun seekTo(position: Long)

    fun addSeekListener(seekListener: SeekListener)

    fun addPlayerStateListener(stateListener: StateListener)

    fun addErrorListener(errorListener: ErrorListener)

    fun addProgressListener(progressListener: ProgressListener)
}