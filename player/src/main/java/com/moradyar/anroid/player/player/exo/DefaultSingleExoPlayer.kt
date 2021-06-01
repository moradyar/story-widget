package com.moradyar.anroid.player.player.exo

import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.EventLogger
import com.moradyar.anroid.player.common.Playable

class DefaultSingleExoPlayer(
    simpleExoPlayer: SimpleExoPlayer,
    eventLogger: EventLogger
) : BaseExoPlayer(simpleExoPlayer), SingleExoPlayer {

    init {
        simpleExoPlayer.addListener(this)
        simpleExoPlayer.addAnalyticsListener(this)
        simpleExoPlayer.addAnalyticsListener(eventLogger)
    }

    override fun play(playable: Playable) {
        prepareAndPlay(playable)
    }
}