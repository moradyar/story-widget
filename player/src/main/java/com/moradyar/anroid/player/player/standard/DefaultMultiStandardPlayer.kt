package com.moradyar.anroid.player.player.standard

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.SurfaceHolder
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.PlayerState
import com.moradyar.anroid.player.common.RepeatModeEnum
import com.moradyar.anroid.player.common.Transition
import com.moradyar.anroid.player.errors.PlayerError
import com.moradyar.anroid.player.listeners.TransitionListener
import java.lang.ref.WeakReference

class DefaultMultiStandardPlayer : BaseStandardPlayer(),
    MultiStandardPlayer,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener {

    private val transitionListeners: MutableList<WeakReference<TransitionListener>> =
        mutableListOf()
    private val playableList: MutableList<Playable> = mutableListOf()
    private var currentIndex = 0
    private var repeatMode: RepeatModeEnum = RepeatModeEnum.ALL
    private var shuffleMode: Boolean = false
    private var surfaceHolder: SurfaceHolder? = null

    override fun play(playableList: Array<Playable>, surfaceHolder: SurfaceHolder?) {
        this.surfaceHolder = surfaceHolder
        initPlayableList(playableList)
        playableList.getOrNull(currentIndex)?.let { playable ->
            play(playable, surfaceHolder)
        }
    }

    private fun play(playable: Playable, surfaceHolder: SurfaceHolder?) {
        player = MediaPlayer().apply {
            playerStateListeners.forEach { it.get()?.onEvent(PlayerState.Idle, playable) }
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnSeekCompleteListener(this@DefaultMultiStandardPlayer)
            setOnBufferingUpdateListener(this@DefaultMultiStandardPlayer)
            setOnErrorListener(this@DefaultMultiStandardPlayer)
            setOnInfoListener(this@DefaultMultiStandardPlayer)
            setDataSource(playable.uri)
            setOnPreparedListener(this@DefaultMultiStandardPlayer)
            setOnCompletionListener(this@DefaultMultiStandardPlayer)
            surfaceHolder?.let { setDisplay(it) }
            prepareAsync()
        }
    }

    private fun initPlayableList(playableList: Array<Playable>) {
        this.playableList.addAll(playableList)
        currentIndex = 0
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

    override var currentPlayable: Playable? = getItemAtIndex(currentIndex)

    override fun onPrepared(mp: MediaPlayer?) {
        resume()
        updateProgress()
    }

    override fun getItemAtIndex(index: Int): Playable? = playableList.getOrNull(index)

    override fun getPlaylistSize(): Int = playableList.size

    override fun hasNext(): Boolean = currentIndex != playableList.size - 1

    override fun hasPrevious(): Boolean = currentIndex != 0

    override fun playNext() {
        if (hasNext()) {
            currentIndex++
            playAtIndex(currentIndex)
        }
    }

    override fun playPrevious() {
        if (hasPrevious()) {
            currentIndex--
            playAtIndex(currentIndex)
        }
    }

    private fun playAtIndex(index: Int) {
        getItemAtIndex(index)?.let {
            stop()
            release()
            play(it, surfaceHolder)
            handleCallingStateListeners(it)
        }
    }

    override fun setRepeatMode(repeatModeEnum: RepeatModeEnum) {
        this.repeatMode = repeatModeEnum
    }

    override fun setShuffleMode(onOrOff: Boolean) {
        this.shuffleMode = onOrOff
        val currentPlayableItem = currentPlayable
        playableList.shuffle()
        currentIndex = playableList.indexOfFirst { it.uri == currentPlayableItem?.uri }
    }

    override fun addTransitionListener(listener: TransitionListener) {
        transitionListeners.add(WeakReference(listener))
    }

    override fun onCompletion(mp: MediaPlayer?) {
        when (repeatMode) {
            RepeatModeEnum.ALL -> {
                if (!hasNext()) {
                    currentIndex = 0
                    playAtIndex(currentIndex)
                } else {
                    playNext()
                }
            }
            RepeatModeEnum.ONE -> {
                playAtIndex(currentIndex)
            }
            RepeatModeEnum.NONE -> {
                if (hasNext()) {
                    playNext()
                } else {
                    currentPlayable?.let { playable ->
                        playerStateListeners.forEach {
                            it.get()?.onEvent(PlayerState.Ended, playable)
                        }
                    }
                }
            }
        }

    }

    private fun handleCallingStateListeners(playable: Playable) {
        transitionListeners.forEach {
            it.get()?.onTransition(Transition.Standard.Changed(playable))
        }
    }
}