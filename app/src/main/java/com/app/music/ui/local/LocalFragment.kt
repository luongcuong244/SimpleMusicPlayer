package com.app.music.ui.local

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.music.adapter.SongAdapter
import com.app.music.bottomsheet.SongBottomSheet
import com.app.music.database.AppDatabase
import com.app.music.database.entity.SongModel
import com.app.music.databinding.FragmentMusicLocalBinding
import com.app.music.ui.playsong.PlaySongActivity
import com.app.music.utils.Constant
import com.app.music.utils.FileUtils
import com.app.music.utils.PermissionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

class LocalFragment : Fragment() {
    private lateinit var binding: FragmentMusicLocalBinding
    private var adapter: SongAdapter? = null
    private val songs: MutableList<SongModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMusicLocalBinding.inflate(layoutInflater)
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
        checkPermissionAndUpdateData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnGrantPermission.setOnClickListener {
            PermissionUtils.requestReadAudioPermission(requireActivity(), REQUEST_CODE_PERMISSION)
        }
        binding.ivRefresh.setOnClickListener {
            if (PermissionUtils.isReadAudioPermissionGranted(requireContext())) {
                refreshData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.isReadAudioPermissionGranted(requireContext())) {
            binding.btnGrantPermission.visibility = View.GONE
            if (songs.isEmpty()) {
                binding.rvSongs.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                binding.rvSongs.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
            }
        } else {
            binding.btnGrantPermission.visibility = View.VISIBLE
            binding.rvSongs.visibility = View.GONE
            binding.tvNoData.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            checkPermissionAndUpdateData()
        }
    }

    private fun checkPermissionAndUpdateData() {
        if (PermissionUtils.isReadAudioPermissionGranted(requireActivity())) {
            refreshData()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            songs.clear()
            songs.addAll(FileUtils.getAllAudios(requireContext()))
            if (adapter != null) {
                adapter?.notifyDataSetChanged()
            } else {
                adapter = SongAdapter(
                    songs,
                    showMoreIcon = false,
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

    private companion object {
        const val REQUEST_CODE_PERMISSION = 100
    }
}