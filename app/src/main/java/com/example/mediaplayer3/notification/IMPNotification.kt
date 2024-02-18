package com.example.mediaplayer3.notification

import android.app.Notification
import android.content.Context

interface IMPNotification {

    fun showNotification(context: Context)

    fun createNotification(context: Context): Notification

    fun cancelNotification(context: Context)
}