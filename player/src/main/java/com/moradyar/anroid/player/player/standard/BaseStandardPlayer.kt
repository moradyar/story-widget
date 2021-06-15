package com.moradyar.anroid.player.player.standard

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.PlayerState
import com.moradyar.anroid.player.common.Progress
import com.moradyar.anroid.player.listeners.ErrorListener
import com.moradyar.anroid.player.listeners.ProgressListener
import com.moradyar.anroid.player.listeners.SeekListener
import com.moradyar.anroid.player.listeners.StateListener
import java.lang.ref.WeakReference


abstract class BaseStandardPlayer : StandardPlayer {

    protected var player: MediaPlayer? = null
    protected val seekListeners: MutableList<WeakReference<SeekListener>> = mutableListOf()
    protected val playerStateListeners: MutableList<WeakReference<StateListener>> = mutableListOf()
    protected val errorListeners: MutableList<WeakReference<ErrorListener>> = mutableListOf()
    private val progressListeners: MutableList<WeakReference<ProgressListener>> = mutableListOf()
    protected var bufferedPositionPercent: Int = 0

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable: () -> Unit = ::updateProgress

    protected fun updateProgress() {
        val duration = player?.duration?.toLong() ?: 0
        val position = player?.currentPosition?.toLong() ?: 0
        try {
            val progress = Progress(
                100,
                position * 100 / duration,
                bufferedPositionPercent.toLong()
            )
            progressListeners.forEach {
                it.get()
                    ?.onProgressChanged(progress)
            }
            handler.removeCallbacks(updateProgressRunnable)
            handler.postDelayed(updateProgressRunnable, UPDATE_DELAY_IN_MILLIS)
        } catch (e: Exception) {
            handler.removeCallbacks(updateProgressRunnable)
            e.printStackTrace()
        }
    }

    protected fun handleAspectRatio(
        surfaceView: SurfaceView,
        mVideoWidth: Float,
        mVideoHeight: Float
    ) {
        try {
            val viewWidth = surfaceView.width
            val viewHeight = surfaceView.height

            var scaleX = 1.0f
            var scaleY = 1.0f

            if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
                scaleX = mVideoWidth / viewWidth
                scaleY = mVideoHeight / viewHeight
            } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
                scaleY = viewWidth / mVideoWidth
                scaleX = viewHeight / mVideoHeight
            } else if (viewWidth > mVideoWidth) {
                scaleY = viewWidth / mVideoWidth / (viewHeight / mVideoHeight)
            } else if (viewHeight > mVideoHeight) {
                scaleX = viewHeight / mVideoHeight / (viewWidth / mVideoWidth)
            }

            val pivotPointX: Int = (viewWidth / 2)
            val pivotPointY: Int = (viewHeight / 2)

            surfaceView.scaleX = scaleX
            surfaceView.pivotX = pivotPointX.toFloat()
            surfaceView.scaleY = scaleY
            surfaceView.pivotY = pivotPointY.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun setMediaSource(context: Context, mediaPlayer: MediaPlayer, playable: Playable) {
        if (playable.uri.startsWith(HTTP_SCHEME))
            mediaPlayer.setDataSource(playable.uri)
        else
            mediaPlayer.setDataSource(context, Uri.parse(playable.uri))
    }

    override fun pause() {
        player?.pause()
        callPlayerStateListeners(PlayerState.Playing(false))
    }

    override fun stop() {
        player?.stop()
        callPlayerStateListeners(PlayerState.Ended)
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun resume() {
        player?.start()
        callPlayerStateListeners(PlayerState.Playing(true))
    }

    override fun playPause() {
        player?.let {
            if (it.isPlaying) {
                pause()
            } else {
                resume()
            }
        }
    }

    override fun release() {
        player?.release()
        player = null
        callPlayerStateListeners(PlayerState.Ended)
    }

    override fun seekTo(position: Long) {
        player?.seekTo((position / SECONDS_IN_MILLIS).toInt())
    }

    override fun addSeekListener(seekListener: SeekListener) {
        seekListeners.add(WeakReference(seekListener))
    }

    override fun addPlayerStateListener(stateListener: StateListener) {
        playerStateListeners.add(WeakReference(stateListener))
    }

    override fun addProgressListener(progressListener: ProgressListener) {
        progressListeners.add(WeakReference(progressListener))
    }

    private fun callPlayerStateListeners(playerState: PlayerState) {
        currentPlayable?.let { playable ->
            playerStateListeners.forEach {
                it.get()?.onEvent(playerState, playable)
            }
        }
    }

    override fun addErrorListener(errorListener: ErrorListener) {
        errorListeners.add(WeakReference(errorListener))
    }

    protected abstract var currentPlayable: Playable?

    companion object {
        private const val SECONDS_IN_MILLIS = 1_000
        private const val UPDATE_DELAY_IN_MILLIS: Long = 1_000
        private const val HTTP_SCHEME = "http"
    }

}