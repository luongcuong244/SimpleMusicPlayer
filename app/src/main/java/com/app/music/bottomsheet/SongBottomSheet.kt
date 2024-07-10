package com.app.music.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import com.app.music.database.AppDatabase
import com.app.music.database.entity.SongModel
import com.app.music.databinding.BottomSheetSongOptionBinding
import com.app.music.utils.ShareUtils
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongBottomSheet(
    private val song: SongModel,
    private val onDeleted: (() -> Unit)? = null,
    private val onChangedFavorite: (() -> Unit)? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSongOptionBinding

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        binding = BottomSheetSongOptionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        initView()
        setupListeners()
    }

    private fun initView() {
        binding.tvName.text = song.name
        binding.tvArtist.text = song.artist
        Glide.with(requireContext())
            .load(song.thumbnailLink)
            .into(binding.ivThumbnail)
        if (song.isFavourite) {
            binding.llAddToFavouriteList.visibility = View.GONE
            binding.llDeleteFromFavouriteList.visibility = View.VISIBLE
        } else {
            binding.llAddToFavouriteList.visibility = View.VISIBLE
            binding.llDeleteFromFavouriteList.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.llAddToFavouriteList.setOnClickListener {
            song.isFavourite = true
            AppDatabase.INSTANCE.songDao().update(song)
            onChangedFavorite?.invoke()
            dismiss()
        }
        binding.llDeleteFromFavouriteList.setOnClickListener {
            song.isFavourite = false
            AppDatabase.INSTANCE.songDao().update(song)
            onChangedFavorite?.invoke()
            dismiss()
        }
        binding.llShare.setOnClickListener {
            ShareUtils.shareSong(requireContext(), song)
            dismiss()
        }
        binding.llDelete.setOnClickListener {
            AppDatabase.INSTANCE.songDao().delete(song)
            onDeleted?.invoke()
            dismiss()
        }
    }
}