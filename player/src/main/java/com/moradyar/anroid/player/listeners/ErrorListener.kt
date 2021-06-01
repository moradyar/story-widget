package com.moradyar.anroid.player.listeners

import com.moradyar.anroid.player.errors.PlayerError

interface ErrorListener {
    fun onError(error: PlayerError)
}