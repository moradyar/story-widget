package com.moradyar.anroid.exoplayerexample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.moradyar.anroid.exoplayerexample.databinding.ActivityMainBinding
import com.moradyar.anroid.player.common.Playable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val playableList by lazy {
        arrayOf(
            Playable(
                uri = getContentUri(R.raw.vid1),
                title = "First"
            ),
            Playable(
                uri = getContentUri(R.raw.vid2),
                title = "Second"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.btnShowStoryStandard.setOnClickListener {
            StoryDialogStandard(playableList).show(
                supportFragmentManager,
                STANDARD_STORY_DIALOG_TAG
            )
        }
        binding.btnShowStoryExo.setOnClickListener {
            StoryDialogExo(playableList).show(supportFragmentManager, EXO_STORY_DIALOG_TAG)
        }
    }

    private fun getContentUri(@RawRes resId: Int) = "android.resource://$packageName/$resId"

    companion object {
        private const val STANDARD_STORY_DIALOG_TAG = "STANDARD_STORY_DIALOG_TAG"
        private const val EXO_STORY_DIALOG_TAG = "EXO_STORY_DIALOG_TAG"
    }
}