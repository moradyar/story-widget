package com.moradyar.anroid.player.common

data class Clip(
    val startPositionInMillis: Long,
    val endPositionInMillis: Long
) {
    companion object {
        fun emptyClip(): Clip {
            return Clip(
                startPositionInMillis = -1,
                endPositionInMillis = -1
            )
        }
    }
}
