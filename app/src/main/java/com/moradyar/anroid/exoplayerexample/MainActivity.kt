package com.moradyar.anroid.exoplayerexample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.moradyar.anroid.exoplayerexample.databinding.ActivityMainBinding
import com.moradyar.anroid.player.common.Playable

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val playableList = arrayOf(
        Playable(
            uri = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8",
            title = "First"
        ),
        Playable(
            uri = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            title = "Second"
        ),
        Playable(
            uri = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8",
            title = "Third"
        ),
        Playable(
            uri = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8",
            title = "Forth"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.btnShowStory.setOnClickListener {
            StoryDialog(playableList).show(supportFragmentManager, "TAG")
        }
    }
}