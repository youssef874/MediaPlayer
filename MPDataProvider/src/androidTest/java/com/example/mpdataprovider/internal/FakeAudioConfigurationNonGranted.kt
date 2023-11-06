package com.example.mpdataprovider.internal

import android.content.Context
import com.example.mpdataprovider.ContentProvider.internal.IAudioConfiguration

class FakeAudioConfigurationNonGranted: IAudioConfiguration {
    override fun askRequiredPermission(context: Context): List<String> {
        return listOf("audio_permission")
    }
}