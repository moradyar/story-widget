package com.moradyar.anroid.player.player.standard

import android.view.SurfaceHolder
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.RepeatModeEnum
import com.moradyar.anroid.player.listeners.TransitionListener

interface MultiStandardPlayer : StandardPlayer {

    fun play(playableList: Array<Playable>, surfaceHolder: SurfaceHolder? = null)

    fun hasPrevious(): Boolean

    fun hasNext(): Boolean

    fun playNext()

    fun playPrevious()

    fun getPlaylistSize(): Int

    fun getItemAtIndex(index: Int): Playable?

    fun setRepeatMode(repeatModeEnum: RepeatModeEnum)

    fun setShuffleMode(onOrOff: Boolean)

    fun addTransitionListener(listener: TransitionListener)

    class Factory {

        companion object {
            fun create(): MultiStandardPlayer {
                return DefaultMultiStandardPlayer()
            }
        }
    }
}