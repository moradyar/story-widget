package com.moradyar.anroid.player.listeners

interface SeekListener {

    fun onPositionDiscontinuity(newPositionInMillis: Long)
}