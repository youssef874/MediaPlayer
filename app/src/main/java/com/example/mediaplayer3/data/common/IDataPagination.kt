package com.example.mediaplayer3.data.common

interface IDataPagination<ITEM,KEY> {

    suspend fun loadNextItem()

    fun reset()
}