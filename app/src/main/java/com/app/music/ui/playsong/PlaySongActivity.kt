package com.app.music.ui.playsong

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.app.music.R
import com.app.music.database.entity.SongModel
import com.app.music.databinding.ActivityPlaySongBinding
import com.app.music.utils.Constant
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlaySongActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaySongBinding

    private var simpleExoPlayer: ExoPlayer? = null
    private val songs: MutableList<SongModel> = mutableListOf()
    private var currentIndexOfSong = 0

    private var ivPrevious: ImageView? = null
    private var ivNext: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setupExoPlayer()
        setupListeners()
    }

    private fun initView() {
        ivPrevious = findViewById(R.id.iv_previous)
        ivNext = findViewById(R.id.iv_next)
    }

    private fun setupExoPlayer() {
        simpleExoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = simpleExoPlayer
        songs.addAll(intent.getSerializableExtra(Constant.BUNDLE_SONGS) as MutableList<SongModel>)
        currentIndexOfSong = intent.getIntExtra(Constant.BUNDLE_START_FROM_INDEX, 0)
        playSongAt(currentIndexOfSong)
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        ivPrevious?.setOnClickListener {
            nextSong()
        }
        ivNext?.setOnClickListener {
            previousSong()
        }
    }

    private fun nextSong() {
        currentIndexOfSong++
        if (currentIndexOfSong >= songs.size) {
            currentIndexOfSong = 0
        }
        playSongAt(currentIndexOfSong)
    }

    private fun previousSong() {
        currentIndexOfSong--
        if (currentIndexOfSong < 0) {
            currentIndexOfSong = songs.size - 1
        }
        playSongAt(currentIndexOfSong)
    }

    private fun playSongAt(index: Int) {
        val song = songs[index]
        simpleExoPlayer?.stop()
        val mediaItem = MediaItem.fromUri(song.musicLink)
        simpleExoPlayer?.setMediaItem(mediaItem)
        simpleExoPlayer?.prepare()
        simpleExoPlayer?.playWhenReady = true
        updateUIBasedOnCurrentSong(song)
    }

    private fun updateUIBasedOnCurrentSong(songModel: SongModel) {
        binding.tvSongName.text = songModel.name
        binding.tvArtist.text = songModel.artist
        Glide.with(this)
            .load(songModel.thumbnailLink)
            .placeholder(R.drawable.default_song_thumbnail)
            .into(binding.ivThumbnail)
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer?.release()
    }
}