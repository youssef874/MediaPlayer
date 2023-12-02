package com.example.mediaplayer3.data.common

import com.example.mediaplayer3.data.entity.Result

class DefaultPagination<ITEM,KEY>(
    private val initialKey: KEY,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: KEY)->Result<List<ITEM>>,
    private inline val getNextKey: suspend (List<ITEM>)->KEY,
    private inline val onError: suspend (Throwable?)->Unit,
    private inline val onSuccess: suspend (items: List<ITEM>,newKey: KEY)->Unit
): IDataPagination<ITEM,KEY> {

    private var currentKey = initialKey
    private var isMakingRequest = false
    override suspend fun loadNextItem() {
        if (isMakingRequest){
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        val items = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return@getOrElse
        }
        items?.let {
            currentKey = getNextKey(it)
            onSuccess(it,currentKey)
            onLoadUpdated(false)
        }
    }

    private  suspend fun <T> Result<T>.getOrElse(onError: suspend (throwable: Throwable)->Unit): T?{
        return when(this){
            is Result.Success->{
                data
            }

            is Result.Error->{
                onError(t)
                null
            }

        }
    }

    override fun reset() {
        currentKey = initialKey
    }
}