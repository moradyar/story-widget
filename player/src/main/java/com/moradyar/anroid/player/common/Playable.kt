package com.moradyar.anroid.player.common

import com.moradyar.anroid.player.common.Clip.Companion.emptyClip

data class Playable(
    val uri: String,
    val title: String = "",
    val triggers: List<PlayableTrigger> = listOf(),
    val subtitles: List<Subtitle> = listOf(),
    val clip: Clip = emptyClip(),
    val adTagUri: String = ""
)
