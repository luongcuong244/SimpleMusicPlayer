package com.app.music.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.app.music.database.entity.SongModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    fun getAllAudios(context: Context): MutableList<SongModel> {
        val audios = mutableListOf<SongModel>()

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use { cursor ->
            val pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            while (cursor.moveToNext()) {
                val path = cursor.getString(pathColumnIndex)
                if (File(path).exists()) {
                    audios.add(
                        SongModel(
                            name = File(path).nameWithoutExtension,
                            artist = "Unknown",
                            thumbnailLink = "",
                            musicLink = path,
                        )
                    )
                }
            }
        }
        return audios
    }

    fun saveSongToCacheDir(
        context: Context,
        song: SongModel,
        onDownloadSuccess: (File) -> Unit,
        onDownloadFailed: () -> Unit
    ) {
        // Save online song to cache dir
        val fileName = song.name + "-" + song.id + ".mp3"
        saveMp3ToCacheDir(
            context,
            song.musicLink,
            fileName,
            onDownloadSuccess,
            onDownloadFailed
        )
    }

    fun saveMp3ToCacheDir(
        context: Context,
        url: String,
        fileName: String,
        onDownloadSuccess: (File) -> Unit,
        onDownloadFailed: () -> Unit
    ) {
        try {
            val file = File(context.cacheDir, fileName)
            if (file.exists()) {
                onDownloadSuccess.invoke(file)
                return
            }

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d("FileUtils", "onFailure: ${e.message}")
                    CoroutineScope(Dispatchers.Main).launch {
                        onDownloadFailed.invoke()
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    val responseBody = response.body
                    if (responseBody != null) {
                        val inputStream = responseBody.byteStream()
                        val outputStream = FileOutputStream(file)
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        outputStream.close()
                        inputStream.close()
                        CoroutineScope(Dispatchers.Main).launch {
                            onDownloadSuccess.invoke(file)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            onDownloadFailed.invoke()
        }
    }

    fun getUriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    }
}