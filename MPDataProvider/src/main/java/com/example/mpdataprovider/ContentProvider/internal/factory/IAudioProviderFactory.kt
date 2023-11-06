package com.example.mpdataprovider.ContentProvider.internal.factory

import android.content.Context
import com.example.mpdataprovider.ContentProvider.internal.IAudioProvider

internal interface IAudioProviderFactory {

    fun create(context: Context): IAudioProvider
}