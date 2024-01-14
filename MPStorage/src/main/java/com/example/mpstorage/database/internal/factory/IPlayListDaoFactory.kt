package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.IPlayListDao

internal interface IPlayListDaoFactory {

    fun create(context: Context): IPlayListDao
}