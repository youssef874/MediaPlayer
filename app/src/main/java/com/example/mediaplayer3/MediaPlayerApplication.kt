package com.example.mediaplayer3

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MediaPlayerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}