package com.example.mpstorage.database.data

data class DBPlayListData(
    val playListId: Long = 0,
    val playListName: String = ""
): BaseDatabaseData(playListId)
