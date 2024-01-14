package com.example.mpstorage.database.internal

import com.example.mpstorage.database.data.DBPlayListData
import com.example.mpstorage.database.data.PlayListQuery
import com.example.mpstorage.database.data.SearchPlayList
import com.example.mpstorage.database.internal.entity.PlayListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PlayListDataProviderImpl(private val playListDao: IPlayListDao): IPlayListDataProvider {

    override suspend fun add(data: DBPlayListData) {
        playListDao.addPlayList(data.toPlayListEntity())
    }

    override suspend fun update(data: DBPlayListData) {
        playListDao.updatePlayList(data.toPlayListEntity())
    }

    override suspend fun delete(data: DBPlayListData) {
        playListDao.deletePlayList(data.toPlayListEntity())
    }

    override fun observeAll(): Flow<List<DBPlayListData>> {
        return playListDao.observeAll().map { value: List<PlayListEntity> -> value.map { it.toDBPlayListData() } }
    }

    override suspend fun getAll(): List<DBPlayListData> {
        return playListDao.getAllPlayList().map { it.toDBPlayListData() }
    }

    override fun observeById(id: Long): Flow<DBPlayListData?> {
        return playListDao.observePlayList(id).map { it?.toDBPlayListData() }
    }

    override suspend fun getById(id: Long): DBPlayListData? {
        return playListDao.getPlayListById(id)?.toDBPlayListData()
    }

    override suspend fun query(playListQuery: PlayListQuery) {
        playListQuery.toInternalPlayListQuery(playListDao).action()
    }

    override fun query(query: SearchPlayList): Flow<List<DBPlayListData>> {
        return query.toInternalPlayListFinder(playListDao).observe()
    }
}