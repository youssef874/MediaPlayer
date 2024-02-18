package com.example.mediaplayer3.notification

import androidx.media3.session.MediaSession
import com.example.mediaplayer3.domain.entity.UiAudio

object NotificationManager {

    private const val MEDIA_PLAYER_CHANNEL_ID = 1L
    const val MEDIA_PLAYER_NOTIFICATION_ID = 11L
    fun getMediaNotification( uiAudio: UiAudio, mediaSession: MediaSession): IMPNotification {
        return MediaPlayerMPNotificationImpl(
            channelId = MEDIA_PLAYER_CHANNEL_ID,
            notificationId = MEDIA_PLAYER_NOTIFICATION_ID,
            uiAudio = uiAudio,
            mediaSession = mediaSession
        )
    }
}