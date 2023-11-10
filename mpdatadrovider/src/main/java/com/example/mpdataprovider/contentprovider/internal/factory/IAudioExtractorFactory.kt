package com.example.mpdataprovider.contentprovider.internal.factory

import android.content.Context
import com.example.mpdataprovider.contentprovider.internal.IAudioExtractor

/**
 * A factory for [IAudioExtractor]
 */
internal interface IAudioExtractorFactory {

    fun create(context: Context): IAudioExtractor
}