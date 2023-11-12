package com.example.mpstorage.synchronizer.factory

import android.content.Context
import com.example.mpstorage.database.internal.factory.AudioDataProviderFactory
import com.example.mpstorage.synchronizer.internal.DataBaseSynchronize
import com.example.mpstorage.synchronizer.internal.DataSynchroniseManagerImpl
import com.example.mpstorage.synchronizer.internal.IDataSynchronizeManager

internal object DataSynchroniseManagerFactoryImpl : IDataSynchroniseManagerFactory {

    @Volatile
    private var sInstance: IDataSynchronizeManager? = null

    override fun create(context: Context): IDataSynchronizeManager {
        return sInstance ?: synchronized(this) {
            val dataBaseSynchronize = DataBaseSynchronize(AudioDataProviderFactory.create(context))
            val instance = DataSynchroniseManagerImpl
            instance.synchronize(dataBaseSynchronize, context = context)
            sInstance = instance
            instance
        }
    }
}