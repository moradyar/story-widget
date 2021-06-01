package com.moradyar.anroid.player.player.standard

import android.view.SurfaceHolder
import com.moradyar.anroid.player.common.Playable

interface SingleStandardPlayer : StandardPlayer {

    fun play(playable: Playable, surfaceHolder: SurfaceHolder? = null)

    class Factory {
        companion object {

            @JvmStatic
            fun create(): SingleStandardPlayer {
                return DefaultSingleStandardPlayer()
            }
        }
    }
}