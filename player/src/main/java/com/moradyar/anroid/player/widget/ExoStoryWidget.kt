package com.moradyar.anroid.player.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import androidx.constraintlayout.widget.ConstraintLayout
import com.moradyar.anroid.player.common.Playable
import com.moradyar.anroid.player.common.PlayerState
import com.moradyar.anroid.player.common.Progress
import com.moradyar.anroid.player.common.Transition
import com.moradyar.anroid.player.databinding.ExoStoryWidgetBinding
import com.moradyar.anroid.player.listeners.ProgressListener
import com.moradyar.anroid.player.listeners.StateListener
import com.moradyar.anroid.player.listeners.TransitionListener
import com.moradyar.anroid.player.player.exo.MultiExoPlayer

@SuppressLint("ClickableViewAccessibility")
class ExoStoryWidget : ConstraintLayout, TransitionListener, StateListener, ProgressListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var binding: ExoStoryWidgetBinding =
        ExoStoryWidgetBinding.inflate(LayoutInflater.from(context))
    private val player: MultiExoPlayer
    private var onCloseClicked: () -> Unit = {}

    init {
        addView(binding.root)
        player = MultiExoPlayer.Factory.create(context)
        binding.playerView.setOnTouchListener { _, event ->
            when (event.action) {
                ACTION_UP -> {
                    player.resume()
                    true
                }
                ACTION_DOWN -> {
                    player.pause()
                    true
                }
                else -> false
            }
        }
        player.setPlayerView(binding.playerView)
        binding.icClose.setOnClickListener {
            player.stop()
            player.release(binding.playerView)
            onCloseClicked()
        }
        binding.next.setOnClickListener {
            if (player.hasNext()) {
                player.playNext()
            }
        }
        binding.prev.setOnClickListener {
            if (player.hasPrevious()) {
                player.playPrevious()
            }
        }
        player.addPlayerStateListener(this)
        player.addTransitionListener(this)
        player.addProgressListener(this)
    }

    fun play(playableList: Array<Playable>) {
        player.play(playableList)
    }

    fun setOnCloseClicked(onCloseClicked: () -> Unit) {
        this.onCloseClicked = onCloseClicked
    }

    override fun onTransition(transition: Transition) {
        binding.title.text = transition.playable.title
    }

    override fun onEvent(state: PlayerState, playable: Playable) {
        binding.title.text = playable.title
    }

    override fun onProgressChanged(progress: Progress) {
        binding.prg.max = progress.max.toInt()
        binding.prg.progress = progress.current.toInt()
        binding.prg.secondaryProgress = progress.buffer.toInt()
    }
}