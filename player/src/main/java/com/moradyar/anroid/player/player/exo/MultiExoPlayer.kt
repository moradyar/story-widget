package com.moradyar.anroid.player.player.exo

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.EventLogger
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.RepeatModeEnum
import com.moradyar.anroid.player.listeners.TransitionListener

interface MultiExoPlayer : ExoPlayer {

    fun play(playableList: Array<Playable>)

    fun hasPrevious(): Boolean

    fun hasNext(): Boolean

    fun playNext()

    fun playPrevious()

    fun getPlaylistSize(): Int

    fun getItemAtIndex(index: Int): Playable?

    fun getCurrentPlayable(): Playable?

    fun setRepeatMode(repeatModeEnum: RepeatModeEnum)

    fun setShuffleMode(onOrOff: Boolean)

    fun addTransitionListener(listener: TransitionListener)

    class Factory {

        companion object {
            fun create(context: Context): MultiExoPlayer {
                return DefaultMultiExoPlayer(
                    SimpleExoPlayer.Builder(context).build(),
                    EventLogger(null)
                )
            }
        }
    }
}