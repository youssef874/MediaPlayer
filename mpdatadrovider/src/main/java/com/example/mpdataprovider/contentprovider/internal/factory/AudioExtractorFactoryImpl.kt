package com.example.mpdataprovider.contentprovider.internal.factory

import android.content.Context
import com.example.mpdataprovider.contentprovider.internal.AudioExtractorImpl
import com.example.mpdataprovider.contentprovider.internal.IAudioExtractor

internal object AudioExtractorFactoryImpl: IAudioExtractorFactory {

    private var INSTANCE: IAudioExtractor? = null

    override fun create(context: Context): IAudioExtractor {
        return INSTANCE ?: synchronized(this){
            val instance = AudioExtractorImpl(context.contentResolver)
            INSTANCE = instance
            instance
        }
    }
}