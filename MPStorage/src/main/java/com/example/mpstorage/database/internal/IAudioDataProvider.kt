package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.DBAudioData
import com.example.mpstorage.database.data.QueryAudio
import com.example.mpstorage.database.data.SearchAudio

 interface IAudioDataProvider: IBaseDataBaseProvider<DBAudioData, SearchAudio> {

    suspend fun query(queryAudio: QueryAudio)
}