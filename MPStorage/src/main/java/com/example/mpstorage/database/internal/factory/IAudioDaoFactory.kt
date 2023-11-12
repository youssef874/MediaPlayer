package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.internal.entity.IAudioDao

internal interface IAudioDaoFactory {

    fun create(context: Context): IAudioDao
}