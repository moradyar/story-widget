package com.moradyar.anroid.player.player.exo

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.EventLogger
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.RepeatModeEnum
import com.moradyar.anroid.player.common.Transition
import com.moradyar.anroid.player.listeners.TransitionListener
import java.lang.ref.WeakReference

class DefaultMultiExoPlayer(
    simpleExoPlayer: SimpleExoPlayer,
    eventLogger: EventLogger
) : BaseExoPlayer(simpleExoPlayer), MultiExoPlayer {

    private val transitionListeners: MutableList<WeakReference<TransitionListener>> =
        mutableListOf()

    init {
        simpleExoPlayer.addListener(this)
        simpleExoPlayer.addAnalyticsListener(this)
        simpleExoPlayer.addAnalyticsListener(eventLogger)
    }

    override fun play(playableList: Array<Playable>) {
        prepareAndPlay(playableList)
    }

    override fun hasPrevious(): Boolean {
        return simpleExoPlayer.hasPrevious()
    }

    override fun hasNext(): Boolean {
        return simpleExoPlayer.hasNext()
    }

    override fun playNext() {
        simpleExoPlayer.next()
    }

    override fun playPrevious() {
        simpleExoPlayer.previous()
    }

    override fun getPlaylistSize(): Int = simpleExoPlayer.mediaItemCount

    override fun getItemAtIndex(index: Int): Playable? {
        return simpleExoPlayer.getMediaItemAt(index).playbackProperties?.uri?.toString()?.let {
            Playable(it)
        }
    }

    override fun getCurrentPlayable(): Playable? {
        return super.currentPlayable()
    }

    override fun setRepeatMode(repeatModeEnum: RepeatModeEnum) {
        simpleExoPlayer.repeatMode = when (repeatModeEnum) {
            RepeatModeEnum.ALL -> REPEAT_MODE_ALL
            RepeatModeEnum.ONE -> REPEAT_MODE_ONE
            RepeatModeEnum.NONE -> REPEAT_MODE_OFF
        }
    }

    override fun setShuffleMode(onOrOff: Boolean) {
        simpleExoPlayer.shuffleModeEnabled = onOrOff
    }

    override fun addTransitionListener(listener: TransitionListener) {
        transitionListeners.add(WeakReference(listener))
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        currentPlayable()?.let { playable ->
            when (reason) {
                MEDIA_ITEM_TRANSITION_REASON_REPEAT -> {
                    transitionListeners.forEach {
                        it.get()?.onTransition(Transition.ExoTransition.Repeat(playable))
                    }
                }
                MEDIA_ITEM_TRANSITION_REASON_AUTO -> {
                    transitionListeners.forEach {
                        it.get()?.onTransition(Transition.ExoTransition.Auto(playable))
                    }
                }
                MEDIA_ITEM_TRANSITION_REASON_SEEK -> {
                    transitionListeners.forEach {
                        it.get()?.onTransition(Transition.ExoTransition.Seek(playable))
                    }
                }
                MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> {
                    transitionListeners.forEach {
                        it.get()?.onTransition(Transition.ExoTransition.PlaylistChanged(playable))
                    }
                }
            }
        }
    }
}