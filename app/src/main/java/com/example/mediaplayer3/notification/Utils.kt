package com.example.mediaplayer3.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build


fun NotificationManager.createNotificationChannel(
    channelName: String,
    description: String,
    channelId: String,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(channelId, channelName, importance).apply {
                setDescription(description)
            }
        createNotificationChannel(channel)
    }
}