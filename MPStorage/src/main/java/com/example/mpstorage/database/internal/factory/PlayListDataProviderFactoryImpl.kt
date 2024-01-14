package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.IPlayListDataProvider
import com.example.mpstorage.database.internal.PlayListDataProviderImpl

 object PlayListDataProviderFactoryImpl: IPlayListDataProviderFactory {


    override fun create(context: Context): IPlayListDataProvider {
        return PlayListDataProviderImpl(PlayListDaoFactoryImpl.create(context))
    }
}