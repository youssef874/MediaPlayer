package com.example.mediaplayer3.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.graphics.decodeBitmap
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.mediaplayer3.R
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.service.MediaPlayerService
import com.example.mplog.MPLogger

internal class MediaPlayerMPNotificationImpl(
    private val notificationId: Long,
    private val channelId: Long,
    private val uiAudio: UiAudio,
    private val mediaSession: MediaSession
) : IMPNotification {



    override fun showNotification(context: Context) {
        MPLogger.i(
            CLASS_NAME,
            "showNotification",
            TAG,
            "notificationId: $notificationId, channelId: $channelId, uiAudio: $uiAudio"
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId.toInt(), createNotification(context))
    }

    @OptIn(UnstableApi::class) override fun createNotification(context: Context): Notification {
        MPLogger.i(
            CLASS_NAME,
            "createNotification",
            TAG,
            "notificationId: $notificationId, channelId: $channelId uiAudio: $uiAudio"
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            uiAudio.albumThumbnailUri?.let {
                ImageDecoder.createSource(context.contentResolver,
                    it
                ).decodeBitmap{ _, _->}
            }
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver ,uiAudio.albumThumbnailUri)
        }
        val builder = NotificationCompat.Builder(context, channelId.toString())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.baseline_audio_file_24)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession)
                .setShowActionsInCompactView(0,1,2))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle(uiAudio.songName)
            .setContentText(uiAudio.artistName)
            .setLargeIcon(bitmap)
            .setDeleteIntent(getDeletePendingIntent(context))
            .setContentIntent(getContentPendingIntent(context))

        notificationManager.createNotificationChannel(
            channelId = channelId.toString(),
            channelName = context.getString(R.string.music_string),
            description = context.getString(R.string.play_music_string)
        )
        return builder.build()
    }

    @OptIn(UnstableApi::class) private fun getDeletePendingIntent(context: Context): PendingIntent{
        val intent= Intent(context,MediaPlayerService::class.java)
        intent.action = CANCEL_NOTIFICATION
        return PendingIntent.getService(
            context,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    }

    private fun getContentPendingIntent(context: Context): PendingIntent?{
        val intent = Intent(
            Intent.ACTION_VIEW,
            "myapp://example.com/$SONG_ID=${uiAudio.id}".toUri()
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
    override fun cancelNotification(context: Context) {
        MPLogger.i(
            CLASS_NAME,
            "cancelNotification",
            TAG,
            "notificationId: $notificationId, channelId: $channelId"
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId.toInt())
    }


    companion object {
        private const val CLASS_NAME = "MediaPlayerMPNotificationImpl"
        private const val TAG = "BACKGROUND"
        const val CANCEL_NOTIFICATION = "cancel_notification"
        const val SONG_ID = "songId"
    }
}