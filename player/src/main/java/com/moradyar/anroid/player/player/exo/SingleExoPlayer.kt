package com.moradyar.anroid.player.player.exo

import android.content.Context
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.EventLogger
import com.moradyar.anroid.player.common.Playable

interface SingleExoPlayer : ExoPlayer {

    fun play(playable: Playable)

    class Factory {
        companion object {

            @JvmStatic
            fun create(context: Context): SingleExoPlayer {
                return DefaultSingleExoPlayer(
                    SimpleExoPlayer.Builder(context).build(),
                    EventLogger(null)
                )
            }
        }
    }
}