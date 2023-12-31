package com.example.mpdataprovider.contentprovider.internal.factory

import android.content.Context
import android.os.Build
import com.example.mpdataprovider.contentprovider.internal.AudioProviderImpl
import com.example.mpdataprovider.contentprovider.internal.IAudioProvider
import com.example.mpdataprovider.contentprovider.internal.NewAudioConfigurationImpl
import com.example.mpdataprovider.contentprovider.internal.OldAudioConfigurationImpl

internal object AudioProviderFactoryImpl: IAudioProviderFactory {

    override fun create(context: Context): IAudioProvider {
        val audioConfiguration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            NewAudioConfigurationImpl()
        else
            OldAudioConfigurationImpl()
        return AudioProviderImpl(
            AudioExtractorFactoryImpl.create(context),
            audioConfiguration
        )
    }
}