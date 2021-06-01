package com.moradyar.anroid.player.common

sealed class Transition(val playable: Playable) {

    sealed class ExoTransition(playable: Playable) : Transition(playable) {
        class Repeat(playable: Playable) : ExoTransition(playable)
        class Auto(playable: Playable) : ExoTransition(playable)
        class Seek(playable: Playable) : ExoTransition(playable)
        class PlaylistChanged(playable: Playable) : ExoTransition(playable)
    }

    sealed class Standard(playable: Playable) : Transition(playable) {
        class Changed(playable: Playable) : Standard(playable)
    }
}
