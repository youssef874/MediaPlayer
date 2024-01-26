package com.example.mediaplayer3.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.data.common.DefaultPagination
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.IEditSongUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.entity.UiPlayList
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.viewModel.data.playlist.PlayListUiEvent
import com.example.mediaplayer3.viewModel.data.playlist.PlayListUiState
import com.example.mediaplayer3.viewModel.delegates.IJobController
import com.example.mediaplayer3.viewModel.delegates.JobController
import com.example.mplog.MPLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.SQLException
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val fetchDataUseCase: IFetchDataUseCase,
    private val editSongUseCase: IEditSongUseCase
) : BaseViewModel<PlayListUiEvent, PlayListUiState>() {

    private val _uiState = MutableStateFlow(PlayListUiState())
    override val uiState: StateFlow<PlayListUiState>
        get() = _uiState.asStateFlow()

    private val dataList = mutableListOf<UiPlayList>()

    private val pagination = DefaultPagination(
        initialKey = uiState.value.page,
        onLoadUpdated = { isLoading ->
            MPLogger.d(CLASS_NAME, "onLoadUpdated", TAG, "isLoading: $isLoading")
            _uiState.update {
                it.copy(isNextItemLoading = isLoading)
            }
        },
        onRequest = { nextKey ->
            MPLogger.d(CLASS_NAME, "onRequest", TAG, "nextKey: $nextKey")
            val staringIndex = nextKey * Constant.Utils.PAGE_SIZE
            if (staringIndex + Constant.Utils.PAGE_SIZE < dataList.size) {
                val list = dataList.slice(staringIndex until staringIndex + Constant.Utils.PAGE_SIZE)
                list.forEach {
                    dataList.remove(it)
                }
                Result.Success(
                    list
                )
            }else if (dataList.isNotEmpty()){
                val list = mutableListOf<UiPlayList>()
                list.addAll(dataList)
                dataList.clear()
                Result.Success(
                    list
                )
            } else {
                Result.Success(emptyList())
            }
        },
        getNextKey = { list ->
            MPLogger.d(CLASS_NAME, "getNextKey", TAG, "items: $list")
            uiState.value.page + 1
        },
        onError = { throwable ->
            MPLogger.w(CLASS_NAME, "onError", TAG, "message: ${throwable?.localizedMessage}")
            _uiState.update {
                it.copy(isError = true, dataList = emptyList())
            }
        },
        onSuccess = { items, newKey ->
            MPLogger.d(CLASS_NAME, "onSuccess", TAG, "items: $items, nextKey: $newKey")
            _uiState.update {
                it.copy(
                    dataList = uiState.value.dataList?.plus(items) ?: items,
                    page = newKey,
                    isEndReached = items.isEmpty(),
                )
            }
        }
    )

    init {
        handleEvents()
    }

    private fun handleEvents() {
        viewModelScope.launch {
            uiEvent.collectLatest { event ->
                MPLogger.d(CLASS_NAME, "handleEvents", TAG, "event: $event")
                when (event) {
                    is PlayListUiEvent.LoadData -> {
                        handleLoadDataEvent(event)
                    }

                    is PlayListUiEvent.LoadNextData -> {
                        handleLoadNextDataEvent()
                    }

                    is PlayListUiEvent.AttachSongToPlayList -> {
                        handleAttachSongToPlayListEvent(event)
                    }
                }
            }
        }
    }

    private val attachSongToPlayListJob: IJobController by JobController{args->
        var context: Context? = null
        var songId: Long = 0
        var playListName = ""
        args.forEach {
            when (it) {
                is Context -> {
                    context = it
                }

                is Long -> {
                    songId = it
                }

                is String -> {
                    playListName = it
                }
            }
        }
        context?.let {
            if (songId != 0L){
                editSongUseCase.attachSongToPlayList(
                    context = it,
                    songId = songId,
                    playList = UiPlayList(playListName = playListName)
                )
            }
        }
    }

    private fun handleAttachSongToPlayListEvent(event: PlayListUiEvent.AttachSongToPlayList) {
        MPLogger.i(
            CLASS_NAME,
            "handleAttachSongToPlayListEvent",
            TAG,
            "songId: ${event.songId}, playListName: ${event.playListName}"
        )
        viewModelScope.launch {
            try {
                attachSongToPlayListJob.launchJob(event.songId?:0,event.context,event.playListName)
                _uiState.update {
                    it.copy(isAudioAttachedToPlayList = true)
                }
            }catch (e: SQLException){
                MPLogger.e(CLASS_NAME,"handleAttachSongToPlayListEvent", TAG,"message: ${e.message}")
                _uiState.update {
                    it.copy(isAudioAttachedToPlayList = false)
                }
            }
        }
    }

    private fun handleLoadNextDataEvent() {
        MPLogger.d(
            CLASS_NAME, "handleLoadNextDataEvent",
            TAG, "load next data"
        )
        loadNexData()
    }

    private var loadDataJob: Job? = null

    private fun handleLoadDataEvent(event: PlayListUiEvent.LoadData) {
        MPLogger.i(CLASS_NAME, "handleLoadDataEvent", TAG, "load playlist data")
        _uiState.update {
            it.copy(isAudioAttachedToPlayList = false)
        }

        loadDataJob = viewModelScope.launch {
            try {
                fetchDataUseCase.observeAllPlayList(event.context).stateIn(viewModelScope)
                    .collectLatest {
                        MPLogger.d(CLASS_NAME, "handleLoadDataEvent", TAG, "size: ${it.size}")
                        dataList.addAll(it)
                        loadNexData()
                    }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(isError = true)
                }
            }
        }
    }

    private fun loadNexData() {
        MPLogger.d(
            CLASS_NAME,
            "loadNextList",
            TAG,
            "currentPage: ${uiState.value.page}, dataListSize: ${uiState.value.dataList?.size}"
        )
        viewModelScope.launch {
            pagination.loadNextItem()
        }
    }

    override fun clear() {
        loadDataJob?.cancel()
        attachSongToPlayListJob.cancelJob()
    }

    companion object {
        const val CLASS_NAME = "PlayListViewModel"
        const val TAG = "PLAY_LIST"
    }
}