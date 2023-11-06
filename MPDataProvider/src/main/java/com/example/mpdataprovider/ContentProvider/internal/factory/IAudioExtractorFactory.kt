package com.example.mpdataprovider.ContentProvider.internal.factory

import android.content.Context
import com.example.mpdataprovider.ContentProvider.internal.IAudioExtractor

/**
 * A factory for [IAudioExtractor]
 */
internal interface IAudioExtractorFactory {

    fun create(context: Context): IAudioExtractor
}