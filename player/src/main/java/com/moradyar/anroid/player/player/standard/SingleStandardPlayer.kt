package com.moradyar.anroid.player.player.standard

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.moradyar.anroid.player.common.Playable

interface SingleStandardPlayer : StandardPlayer {

    fun play(
        playable: Playable,
        surfaceView: SurfaceView? = null,
        surfaceHolder: SurfaceHolder? = null
    )

    class Factory {
        companion object {

            @JvmStatic
            fun create(context: Context): SingleStandardPlayer {
                return DefaultSingleStandardPlayer(context)
            }
        }
    }
}