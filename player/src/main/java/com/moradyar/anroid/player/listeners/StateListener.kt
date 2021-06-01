package com.moradyar.anroid.player.listeners

import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.PlayerState

interface StateListener {

    fun onEvent(state: PlayerState, playable: Playable)
}