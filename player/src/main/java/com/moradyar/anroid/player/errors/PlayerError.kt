package com.moradyar.anroid.player.errors

import java.io.IOException

sealed class PlayerError {

    sealed class ExoPlayer {
        class SourceError(val exception: IOException) : PlayerError()
        class RendererError(val exception: Exception) : PlayerError()
        class UnexpectedError(val exception: RuntimeException) : PlayerError()
        object RemoteError : PlayerError()
    }

    sealed class StandardPlayer : PlayerError() {
        object Error : StandardPlayer()
    }
}
