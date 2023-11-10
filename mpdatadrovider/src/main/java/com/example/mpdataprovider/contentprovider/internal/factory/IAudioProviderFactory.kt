package com.example.mpdataprovider.contentprovider.internal.factory

import android.content.Context
import com.example.mpdataprovider.contentprovider.internal.IAudioProvider

internal interface IAudioProviderFactory {

    fun create(context: Context): IAudioProvider
}