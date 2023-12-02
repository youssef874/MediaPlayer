package com.example.mediaplayer3.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.data.common.DefaultPagination
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.FetchDataUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.ResumePauseSongUseCaseImpl
import com.example.mediaplayer3.domain.entity.PlayAudioUseCase
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.ui.Constant
import com.example.mediaplayer3.viewModel.data.tracklist.TrackListUiEvent
import com.example.mediaplayer3.viewModel.data.tracklist.TrackListUiState
import com.example.mediaplayer3.viewModel.delegates.INextOrPreviousItem
import com.example.mediaplayer3.viewModel.delegates.JobController
import com.example.mediaplayer3.viewModel.delegates.NextOrPreviousDelegate
import com.example.mplog.MPLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackListViewModel(
    private val fetchDataUseCase: IFetchDataUseCase = FetchDataUseCase,
    private val playAudioUseCase: IPlayAudioUseCase = PlayAudioUseCase,
    private val pauseOrResumeUseCase: IAudioPauseOrResumeUseCase = ResumePauseSongUseCaseImpl
) : BaseViewModel<TrackListUiEvent, TrackListUiState>() {

    private val _uiState = MutableStateFlow(TrackListUiState())
    override val uiState: StateFlow<TrackListUiState> = _uiState.asStateFlow()

    private val pagination = DefaultPagination(
        initialKey = uiState.value.page,
        onLoadUpdated = { isLoading ->
            MPLogger.d(CLASS_NAME, "onLoadUpdated", TAG, "isLoading: $isLoading")
            _uiState.update {
                it.copy(iNextItemsLoading = isLoading)
            }
        },
        onRequest = { nextKey ->
            MPLogger.d(CLASS_NAME, "onRequest", TAG, "nextKey: $nextKey")
            val staringIndex = nextKey * Constant.Utils.PAGE_SIZE
            if (staringIndex + nextKey < dataList.size) {
                Result.Success(
                    dataList.slice(staringIndex until staringIndex + Constant.Utils.PAGE_SIZE)
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
                it.copy(isError = true, isLoading = false, dataList = emptyList())
            }
        },
        onSuccess = { items, newKey ->
            MPLogger.d(CLASS_NAME, "onSuccess", TAG, "items: $items, nextKey: $newKey")
            _uiState.update {
                it.copy(
                    dataList = uiState.value.dataList + items,
                    page = newKey,
                    isEndReached = items.isEmpty(),
                    isLoading = false
                )
            }
        }
    )

    private val lastPlayingSongJob by JobController { args ->
        var context: Context? = null
        var result: List<UiAudio>? = null
        args.forEach {
            if (it is Context) {
                context = it
            } else if (it is List<*>) {
                result = it as List<UiAudio>
            }
        }
        context?.let { cont ->
            fetchDataUseCase.observeLastPlayingSongId(cont).collectLatest { id ->
                MPLogger.d(CLASS_NAME, "lastPlayingSongJob", TAG, "id: $id")
                if (id != -1L) {
                    val currentAudio = result?.find { uiAudio -> uiAudio.id == id }
                    currentAudio?.let { uiAudio ->
                        _uiState.update {
                            it.copy(
                                currentSelectedItem = uiAudio
                            )
                        }
                    } ?: run {
                        _uiState.update {
                            it.copy(
                                currentSelectedItem = result?.first()
                            )
                        }
                    }
                }
            }
        }
    }

    private val nextOrPreviousDelegate: INextOrPreviousItem<UiAudio> by  NextOrPreviousDelegate()

    init {
        val repo = getAudioDataRepo()
        (fetchDataUseCase as FetchDataUseCase).invoke(repo, viewModelScope)
        (playAudioUseCase as PlayAudioUseCase).invoke(repo, fetchDataUseCase)
        (pauseOrResumeUseCase as ResumePauseSongUseCaseImpl).invoke(repo, playAudioUseCase)
        handleEvent()
        var playJob: Job? = null
        playJob = viewModelScope.launch {
            playAudioUseCase.setOnPlaySongListener(
                job = playJob,
                onPlaySongSuccess = { uiAudio ->
                    _uiState.update {
                        it.copy(
                            currentSelectedItem = uiAudio,
                            isPlaying = true
                        )
                    }
                },
                onPlaySongFailed = { uiAudio ->
                    _uiState.update {
                        it.copy(isPlaying = false)
                    }
                },
            )
        }

        var stopJob: Job? = null
        stopJob = viewModelScope.launch {
            playAudioUseCase.setOnStopListener(stopJob) { _ ->
                _uiState.update {
                    it.copy(isPlaying = false)
                }
            }
        }

        pauseOrResumeUseCase.setOnAudioPauseListener { _ ->
            _uiState.update {
                it.copy(isPlaying = false)
            }
        }
        pauseOrResumeUseCase.setOnAudioResumeListener(
            onAudioResumeSuccess = { _, _ ->
                _uiState.update {
                    it.copy(isPlaying = true)
                }
            },
            onAudioResumeFailed = { _, _ ->
                _uiState.update {
                    it.copy(isPlaying = false)
                }
            }
        )
    }

    private fun handleEvent() {
        viewModelScope.launch {
            uiEvent.collectLatest { event ->
                MPLogger.d(CLASS_NAME, "handleEvent", TAG, "event: $event")
                when (event) {
                    is TrackListUiEvent.LoadData -> {
                        handleLoadDataEvent(event)
                    }

                    is TrackListUiEvent.LoadNextData -> {
                        handleLoadNextDataEvent()
                    }

                    is TrackListUiEvent.ClickSong -> {
                        handleClickSongEvent(event)
                    }

                    is TrackListUiEvent.PlayOrPause -> {
                        handlePlayOrPauseEvent(event)
                    }

                    is TrackListUiEvent.PlayNextSong -> {
                        handlePlayNextSongEvent(event)
                    }

                    is TrackListUiEvent.PlayPreviousSong -> {
                        handlePlayPreviousSongEvent(event)
                    }
                }
            }
        }
    }

    private fun handlePlayPreviousSongEvent(event: TrackListUiEvent.PlayPreviousSong) {
        MPLogger.i(CLASS_NAME, "handlePlayPreviousSongEvent", TAG, "try to play previous song")
        lastPlayingSongJob.cancelJob()
        with(uiState.value) {
            currentSelectedItem?.let {
                nextOrPreviousDelegate.previousItem(
                    list = dataList,
                    currentItem = it
                ){previous: UiAudio ->
                    MPLogger.i(
                        CLASS_NAME,
                        "handlePlayPreviousSongEvent",
                        TAG,
                        "play previous song: $previous"
                    )
                    playAudioUseCase.playSong(event.context, previous, seekTo = 0)
                }
            }
        }
    }

    private fun handlePlayNextSongEvent(event: TrackListUiEvent.PlayNextSong) {
        MPLogger.i(CLASS_NAME, "handlePlayNextSongEvent", TAG, "try to play next song")
        lastPlayingSongJob.cancelJob()
        with(uiState.value) {
            currentSelectedItem?.let {
                nextOrPreviousDelegate.nextItem(
                    list = dataList,
                    currentItem = it
                ){next: UiAudio ->
                    MPLogger.i(CLASS_NAME, "handlePlayNextSongEvent", TAG, "play next Song: $next")
                    playAudioUseCase.playSong(event.context, next, 0)
                }
            }
        }
    }

    private fun handlePlayOrPauseEvent(event: TrackListUiEvent.PlayOrPause) {
        MPLogger.d(CLASS_NAME, "handlePlayOrPauseEvent", TAG, "try to pause or resume")
        lastPlayingSongJob.cancelJob()
        with(uiState.value) {
            if (isPlaying) {
                playAudioUseCase.currentPlayingSong()?.let {
                    if (it.id == currentSelectedItem?.id) {
                        MPLogger.d(CLASS_NAME, "handlePlayOrPauseEvent", TAG, "pause: $it")
                        pauseOrResumeUseCase.pauseSong(event.context, it)
                    } else {
                        MPLogger.w(
                            CLASS_NAME,
                            "handlePlayOrPauseEvent",
                            TAG,
                            "current selected item is not the same as the playing song"
                        )
                    }
                } ?: run {
                    MPLogger.w(
                        CLASS_NAME,
                        "handlePlayOrPauseEvent",
                        TAG,
                        "There no playing song to play"
                    )
                }
            } else {
                playAudioUseCase.currentPlayingSong()?.let {
                    if (it.id == currentSelectedItem?.id) {
                        MPLogger.d(CLASS_NAME, "handlePlayOrPauseEvent", TAG, "resume: $it")
                        pauseOrResumeUseCase.resumeSong(event.context, it)
                    } else {
                        currentSelectedItem?.let { uiAudio ->
                            MPLogger.d(
                                CLASS_NAME,
                                "handlePlayOrPauseEvent",
                                TAG,
                                "the selected song is not the same zs th current song resume: $uiAudio"
                            )
                            playAudioUseCase.playSong(event.context, uiAudio)
                        }
                    }
                } ?: run {
                    currentSelectedItem?.let { uiAudio ->
                        MPLogger.d(
                            CLASS_NAME,
                            "handlePlayOrPauseEvent",
                            TAG,
                            "There no current song so play the selected song play: $uiAudio"
                        )
                        playAudioUseCase.playSong(event.context, uiAudio)
                    }
                }
            }
        }
    }

    private fun handleClickSongEvent(event: TrackListUiEvent.ClickSong) {
        MPLogger.i(CLASS_NAME, "handleClickSongEvent", TAG, "uiAudio: ${event.uiAudio}")
        lastPlayingSongJob.cancelJob()
        playAudioUseCase.stopSong(event.context)
        playAudioUseCase.playSong(event.context, event.uiAudio)
    }

    private fun handleLoadNextDataEvent() {
        MPLogger.d(CLASS_NAME, "handleLoadNextDataEvent", TAG, "load next data")
        lastPlayingSongJob.cancelJob()
        loadNextList()
    }

    private val dataList = mutableListOf<UiAudio>()
    private fun handleLoadDataEvent(event: TrackListUiEvent.LoadData) {
        MPLogger.d(CLASS_NAME, "handleLoadDataEvent", TAG, "event: $event")
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            try {
                fetchDataUseCase.requestData(event.context).collectLatest {
                    MPLogger.d(CLASS_NAME, "handleLoadDataEvent", TAG, "result: $it")
                    dataList.addAll(it)
                    lastPlayingSongJob.launchJob(event.context, it)
                    loadNextList()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isError = true, isLoading = false)
                }
            }
        }
    }

    private fun loadNextList() {
        MPLogger.d(
            CLASS_NAME,
            "loadNextList",
            TAG,
            "currentPage: ${uiState.value.page}, dataListSize: ${uiState.value.dataList.size}"
        )
        viewModelScope.launch {
            pagination.loadNextItem()
        }

    }

    override fun onCleared() {
        super.onCleared()
        lastPlayingSongJob.cancelJob()
    }

    companion object {
        private const val CLASS_NAME = "TrackListViewModel"
        private const val TAG = "TRACK_LIST"
    }
}