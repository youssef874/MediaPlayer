package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.IAudioDao

internal interface IAudioDaoFactory {

    fun create(context: Context): IAudioDao
}