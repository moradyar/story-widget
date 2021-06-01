package com.moradyar.anroid.player.player.exo

import com.google.android.exoplayer2.ui.PlayerView
import com.moradyar.anroid.player.player.common.Player

interface ExoPlayer : Player {

    fun release(playerView: PlayerView?)

    fun setPlayerView(playerView: PlayerView?)
}