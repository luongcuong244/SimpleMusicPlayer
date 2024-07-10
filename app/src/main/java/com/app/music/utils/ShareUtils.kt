package com.app.music.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.widget.Toast
import com.app.music.R
import com.app.music.database.entity.SongModel

object ShareUtils {
    fun shareSong(context: Context, song: SongModel) {
        FileUtils.saveSongToCacheDir(
            context,
            song,
            onDownloadSuccess = { file ->
                val uri = FileUtils.getUriFromFile(context, file)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "audio/mp3"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                val chooser = Intent.createChooser(shareIntent, "Share song")

                // Grant permission to all apps that can handle the intent
                val resInfoList: List<ResolveInfo> = context.packageManager
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                if (shareIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(chooser)
                } else {
                    Toast.makeText(context, context.getString(R.string.no_app_found_to_handle_the_share_action), Toast.LENGTH_SHORT).show()
                }
            },
            onDownloadFailed = {
                Toast.makeText(context, context.getString(R.string.can_t_download_the_song_to_cache), Toast.LENGTH_SHORT).show()
            }
        )
    }
}