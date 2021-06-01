package com.moradyar.anroid.player.player.exo

import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.PlayerState
import com.moradyar.anroid.player.common.Progress
import com.moradyar.anroid.player.errors.PlayerError
import com.moradyar.anroid.player.listeners.ErrorListener
import com.moradyar.anroid.player.listeners.ProgressListener
import com.moradyar.anroid.player.listeners.SeekListener
import com.moradyar.anroid.player.listeners.StateListener
import java.lang.ref.WeakReference

abstract class BaseExoPlayer(
    protected val simpleExoPlayer: SimpleExoPlayer
) : ExoPlayer, Listener, AnalyticsListener {

    private val handler: Handler by lazy {
        Handler(Looper.myLooper() ?: Looper.getMainLooper())
    }
    private val updateProgressRunnable: () -> Unit = ::updateProgress

    private fun updateProgress() {
        val duration: Long = simpleExoPlayer.duration
        val position: Long = simpleExoPlayer.currentPosition
        val bufferedPosition: Long = simpleExoPlayer.bufferedPosition
        progressListeners.forEach {
            it.get()?.onProgressChanged(Progress(duration, position, bufferedPosition))
        }

        handler.removeCallbacks(updateProgressRunnable)
        // Schedule an update if necessary.
        // Schedule an update if necessary.
        val playbackState: Int = simpleExoPlayer.playbackState
        if (playbackState != STATE_IDLE && playbackState != STATE_ENDED) {
            var delayMs: Long
            if (simpleExoPlayer.playWhenReady && playbackState == STATE_READY) {
                delayMs = 1000 - position % 1000
                if (delayMs < 200) {
                    delayMs += 1000
                }
            } else {
                delayMs = 1000
            }
            handler.postDelayed(updateProgressRunnable, delayMs)
        }
    }

    private val stateListeners: MutableList<WeakReference<StateListener>> = mutableListOf()
    private val errorListeners: MutableList<WeakReference<ErrorListener>> = mutableListOf()
    private val seekListeners: MutableList<WeakReference<SeekListener>> = mutableListOf()
    private val progressListeners: MutableList<WeakReference<ProgressListener>> = mutableListOf()

    override fun seekTo(position: Long) {
        simpleExoPlayer.seekTo(position)
    }

    protected fun prepareAndPlay(playable: Playable) {
        setPlayable(playable)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }

    protected fun prepareAndPlay(playableList: Array<Playable>) {
        playableList.forEach { setPlayable(it) }
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
        updateProgress()
    }

    private fun setPlayable(playable: Playable) {
        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(playable.uri)
            .setTag(playable.title)
            .setSubtitles(createSubtitles(playable))
            .apply {
                if (playable.clip.startPositionInMillis != -1L &&
                    playable.clip.endPositionInMillis != -1L
                ) {
                    setClipStartPositionMs(playable.clip.startPositionInMillis)
                    setClipEndPositionMs(playable.clip.endPositionInMillis)
                }
                if (playable.adTagUri.isNotBlank()) {
                    setAdTagUri(playable.adTagUri)
                }
            }.build()
        simpleExoPlayer.addMediaItem(mediaItem)
        initMessages(playable)
    }

    private fun createSubtitles(playable: Playable): List<MediaItem.Subtitle> {
        return playable.subtitles.map {
            MediaItem.Subtitle(
                Uri.parse(it.subtitleUri),
                MimeTypes.APPLICATION_SUBRIP,
                it.language,
                C.SELECTION_FLAG_DEFAULT
            )
        }
    }

    private fun initMessages(
        playable: Playable
    ) {
        playable.triggers.forEach {
            simpleExoPlayer.createMessage { _, payload ->
                if (payload is String) {
                    it.triggerJob(payload)
                }
            }.apply {
                payload = it.payload
                looper = it.looper
                setPosition(it.triggerPositionInMillis)
                deleteAfterDelivery = it.oneTimeTrigger
            }.send()
        }
    }

    override fun addPlayerStateListener(stateListener: StateListener) {
        stateListeners.add(
            WeakReference(
                stateListener
            )
        )
    }

    override fun addProgressListener(progressListener: ProgressListener) {
        progressListeners.add(WeakReference(progressListener))
    }

    override fun addSeekListener(seekListener: SeekListener) {
        seekListeners.add(WeakReference(seekListener))
    }

    override fun addErrorListener(errorListener: ErrorListener) {
        errorListeners.add(WeakReference(errorListener))
    }

    override fun onPlaybackStateChanged(state: Int) {
        currentPlayable()?.let { currentPlayable ->
            stateListeners.forEach {
                when (state) {
                    com.google.android.exoplayer2.ExoPlayer.STATE_IDLE -> it.get()
                        ?.onEvent(PlayerState.Idle, currentPlayable)
                    com.google.android.exoplayer2.ExoPlayer.STATE_READY -> it.get()
                        ?.onEvent(PlayerState.Ready, currentPlayable)
                    com.google.android.exoplayer2.ExoPlayer.STATE_BUFFERING -> it.get()
                        ?.onEvent(PlayerState.Buffering, currentPlayable)
                    com.google.android.exoplayer2.ExoPlayer.STATE_ENDED -> it.get()
                        ?.onEvent(PlayerState.Ended, currentPlayable)
                }
            }
        }
    }

    protected fun currentPlayable(): Playable? {
        return simpleExoPlayer.currentMediaItem?.playbackProperties?.let {
            Playable(it.uri.toString(), (it.tag as String?) ?: "")
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        currentPlayable()?.let { currentPlayable ->
            stateListeners.forEach {
                it.get()?.onEvent(PlayerState.Playing(isPlaying), currentPlayable)
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> {
                errorListeners.forEach {
                    it.get()?.onError(PlayerError.ExoPlayer.SourceError(error.sourceException))
                }
            }
            ExoPlaybackException.TYPE_UNEXPECTED -> {
                errorListeners.forEach {
                    it.get()
                        ?.onError(PlayerError.ExoPlayer.UnexpectedError(error.unexpectedException))
                }
            }
            ExoPlaybackException.TYPE_REMOTE -> {
                errorListeners.forEach { it.get()?.onError(PlayerError.ExoPlayer.RemoteError) }
            }
            ExoPlaybackException.TYPE_RENDERER -> {
                errorListeners.forEach {
                    it.get()?.onError(PlayerError.ExoPlayer.RendererError(error.rendererException))
                }
            }
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: PositionInfo,
        newPosition: PositionInfo,
        reason: Int
    ) {
        seekListeners.forEach {
            it.get()?.onPositionDiscontinuity(newPosition.positionMs)
        }
    }

    override fun setPlayerView(playerView: PlayerView?) {
        playerView?.player = simpleExoPlayer
    }

    override fun pause() {
        simpleExoPlayer.pause()
    }

    override fun stop() {
        simpleExoPlayer.stop()
    }

    override fun resume() {
        simpleExoPlayer.play()
    }

    override fun playPause() {
        if (simpleExoPlayer.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    override fun release(playerView: PlayerView?) {
        simpleExoPlayer.release()
        playerView?.player = null
    }
}