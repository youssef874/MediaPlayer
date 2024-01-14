package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.DBPlayListData
import com.example.mpstorage.database.data.PlayListQuery
import com.example.mpstorage.database.data.SearchPlayList

 interface IPlayListDataProvider: IBaseDataBaseProvider<DBPlayListData,SearchPlayList> {

    suspend fun query(playListQuery: PlayListQuery)
}