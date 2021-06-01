package com.moradyar.anroid.player.common

sealed class PlayerState {
    object Idle : PlayerState()
    object Buffering : PlayerState()
    object Ready : PlayerState()
    object Ended : PlayerState()
    class Playing(val isPlaying: Boolean) : PlayerState()
}
