package com.moradyar.anroid.player.player.standard

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.SurfaceHolder
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.PlayerState
import com.moradyar.anroid.player.errors.PlayerError

class DefaultSingleStandardPlayer : BaseStandardPlayer(),
    SingleStandardPlayer,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener {

    override var currentPlayable: Playable? = null

    override fun play(playable: Playable, surfaceHolder: SurfaceHolder?) {
        currentPlayable = playable
        player = MediaPlayer().apply {
            playerStateListeners.forEach { it.get()?.onEvent(PlayerState.Idle, playable) }
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnSeekCompleteListener(this@DefaultSingleStandardPlayer)
            setOnBufferingUpdateListener(this@DefaultSingleStandardPlayer)
            setOnErrorListener(this@DefaultSingleStandardPlayer)
            setOnInfoListener(this@DefaultSingleStandardPlayer)
            setDataSource(playable.uri)
            setOnPreparedListener(this@DefaultSingleStandardPlayer)
            setOnCompletionListener(this@DefaultSingleStandardPlayer)
            setDisplay(surfaceHolder)
            prepareAsync()
        }
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
        mp?.let { mediaPlayer ->
            seekListeners.forEach {
                it.get()?.onPositionDiscontinuity(mediaPlayer.currentPosition.toLong())
            }
        }
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        bufferedPositionPercent = percent
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        errorListeners.forEach { it.get()?.onError(PlayerError.StandardPlayer.Error) }
        return true
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        val state = when (what) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> PlayerState.Buffering
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> PlayerState.Playing(player?.isPlaying == true)
            else -> PlayerState.Idle
        }
        currentPlayable?.let { playable ->
            playerStateListeners.forEach { it.get()?.onEvent(state, playable) }
        }
        return false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        resume()
        updateProgress()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        currentPlayable?.let { playable ->
            playerStateListeners.forEach { it.get()?.onEvent(PlayerState.Ended, playable) }
        }
    }
}