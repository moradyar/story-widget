package com.moradyar.anroid.exoplayerexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.moradyar.anroid.exoplayerexample.databinding.StoryDialogStandardBinding
import com.moradyar.anroid.player.common.Playable

class StoryDialogStandard(private val playableArray: Array<Playable>) : DialogFragment() {

    private lateinit var binding: StoryDialogStandardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StoryDialogStandardBinding.inflate(LayoutInflater.from(context), container, false)
        binding.player.play(playableArray)
        binding.player.setOnCloseClicked {
            dismiss()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
    }
}