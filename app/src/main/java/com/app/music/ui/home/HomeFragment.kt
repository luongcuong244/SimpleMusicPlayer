package com.app.music.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.music.R
import com.app.music.adapter.SongAdapter
import com.app.music.bottomsheet.SongBottomSheet
import com.app.music.database.AppDatabase
import com.app.music.database.entity.SongModel
import com.app.music.databinding.FragmentHomeBinding
import com.app.music.ui.playsong.PlaySongActivity
import com.app.music.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var adapter: SongAdapter? = null
    private val songs: MutableList<SongModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        refreshData()
    }

    private fun setupListeners() {
        binding.ivAdd.setOnClickListener {
            showAddSongDialog()
        }
    }

    private fun showAddSongDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_song, null)
        val songNameEditText: EditText = dialogView.findViewById(R.id.etSongName)
        val artistNameEditText: EditText = dialogView.findViewById(R.id.etArtistName)
        val thumbnailEditText: EditText = dialogView.findViewById(R.id.etThumbnail)
        val linkEditText: EditText = dialogView.findViewById(R.id.etLink)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val songName = songNameEditText.text.toString().trim()
            val artistName = artistNameEditText.text.toString().trim()
            val thumbnail = thumbnailEditText.text.toString().trim()
            val link = linkEditText.text.toString().trim()

            if (songName.isNotEmpty() && artistName.isNotEmpty() && thumbnail.isNotEmpty() && link.isNotEmpty()) {
                saveSong(songName, artistName, thumbnail, link)
                refreshData()
                alertDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSong(songName: String, artistName: String, thumbnail: String, link: String) {
        AppDatabase.INSTANCE.songDao().insert(
            SongModel(
                name = songName,
                artist = artistName,
                thumbnailLink = thumbnail,
                musicLink = link
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            songs.clear()
            songs.addAll(AppDatabase.INSTANCE.songDao().getAll())
            if (adapter != null) {
                adapter?.notifyDataSetChanged()
            } else {
                adapter = SongAdapter(
                    songs,
                    onSongClick = { song ->
                        val intent = Intent(requireContext(), PlaySongActivity::class.java)
                        intent.putExtra(Constant.BUNDLE_SONGS, songs as Serializable)
                        intent.putExtra(Constant.BUNDLE_START_FROM_INDEX, songs.indexOf(song))
                        startActivity(intent)
                    },
                    onMoreClick = { song ->
                        var bottomSheet: SongBottomSheet? = null
                        bottomSheet = SongBottomSheet(
                            song = song,
                            onDeleted = {
                                refreshData()
                            },
                        )
                        bottomSheet.show(
                            childFragmentManager,
                            bottomSheet.tag
                        )
                    }
                )
                binding.rvSongs.adapter = adapter
            }

            // Hiển thị no data nếu không có dữ liệu
            if (songs.isEmpty()) {
                binding.rvSongs.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.rvSongs.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
            }
        }
    }
}