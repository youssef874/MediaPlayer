package com.example.mpdataprovider.internal

import android.content.Context
import com.example.mpdataprovider.contentprovider.internal.IAudioConfiguration

class FakeAudioConfigurationGranted: IAudioConfiguration {

    override fun askRequiredPermission(context: Context): List<String> {
        return emptyList()
    }
}