package com.app.music.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.music.R
import com.app.music.database.entity.SongModel
import com.app.music.databinding.LayoutItemSongBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class SongAdapter(
    private val songs: List<SongModel>,
    private val showMoreIcon: Boolean = true,
    private val onMoreClick: (SongModel) -> Unit,
    private val onSongClick: (SongModel) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = LayoutItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    inner class SongViewHolder(val binding: LayoutItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongModel) {
            binding.tvTitle.text = song.name
            binding.tvArtist.text = song.artist

            if (song.thumbnailLink.isEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.ivThumbnail.setImageResource(R.drawable.default_song_thumbnail)
            } else {
                binding.progressBar.visibility = View.VISIBLE
                Glide.with(binding.root)
                    .load(song.thumbnailLink)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("SongAdapter", "Glide error: ${e?.message}")
                            binding.progressBar.visibility = View.GONE
                            binding.ivThumbnail.setImageResource(R.drawable.default_song_thumbnail)
                            return false
                        }
                    })
                    .into(binding.ivThumbnail)
            }

            if (showMoreIcon) {
                binding.ivMore.visibility = View.VISIBLE
            } else {
                binding.ivMore.visibility = View.GONE
            }

            binding.ivMore.setOnClickListener {
                onMoreClick(song)
            }

            binding.root.setOnClickListener {
                onSongClick(song)
            }
        }
    }
}