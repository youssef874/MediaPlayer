package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.IPlayListDataProvider

 interface IPlayListDataProviderFactory {

    fun create(context: Context): IPlayListDataProvider
}