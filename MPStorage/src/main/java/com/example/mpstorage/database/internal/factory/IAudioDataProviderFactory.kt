package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.IAudioDataProvider

internal interface IAudioDataProviderFactory {

    fun create(context: Context): IAudioDataProvider
}