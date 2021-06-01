package com.moradyar.anroid.player.common

import android.os.Looper

data class PlayableTrigger(
    val triggerJob: (payload: String) -> Unit,
    val payload: String,
    val looper: Looper,
    val triggerPositionInMillis: Long,
    val oneTimeTrigger: Boolean
)
