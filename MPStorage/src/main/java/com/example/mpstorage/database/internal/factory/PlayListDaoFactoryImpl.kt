package com.example.mpstorage.database.internal.factory

import android.content.Context
import com.example.mpstorage.database.internal.DataBase
import com.example.mpstorage.database.internal.IPlayListDao

internal object PlayListDaoFactoryImpl: IPlayListDaoFactory {
    override fun create(context: Context): IPlayListDao {
        return DataBase.create(context).getPlayListDao()
    }
}