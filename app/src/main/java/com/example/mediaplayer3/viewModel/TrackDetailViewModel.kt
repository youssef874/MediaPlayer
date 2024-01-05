package com.example.mediaplayer3.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mediaplayer3.domain.IAudioConfiguratorUseCase
import com.example.mediaplayer3.domain.IAudioForwardOrRewindUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IEditSongUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.IPlayNextOrPreviousSongUseCase
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailUiState
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailsUiEvent
import com.example.mediaplayer3.viewModel.delegates.IJobController
import com.example.mediaplayer3.viewModel.delegates.JobController
import com.example.mplog.MPLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val fetchDataUseCase: IFetchDataUseCase,
    private val playAudioUseCase: IPlayAudioUseCase ,
    private val pauseOrResumeUseCase: IAudioPauseOrResumeUseCase ,
    private val audioConfiguratorUseCase: IAudioConfiguratorUseCase ,
    private val audioForwardOrRewindUseCase: IAudioForwardOrRewindUseCase,
    private val playNextOrPreviousSongUseCase: IPlayNextOrPreviousSongUseCase,
    private val editSongUseCase: IEditSongUseCase
) : BaseViewModel<TrackDetailsUiEvent, TrackDetailUiState>() {

    private val _uiState = MutableStateFlow(TrackDetailUiState())
    override val uiState: StateFlow<TrackDetailUiState> = _uiState.asStateFlow()


    private val collectIsRandomJob: IJobController by JobController { args ->
        var context: Context? = null
        args.forEach {
            if (it is Context) {
                context = it
            }
        }
        context?.let { cont ->
            audioConfiguratorUseCase.isRandomModeInFlow(cont).collectLatest { isInRandom ->
                MPLogger.d(CLASS_NAME, "collectIsRandomJob", TAG, "isRandom: $isInRandom")
                _uiState.update {
                    it.copy(isInRandomMode = isInRandom)
                }
            }
        }
    }

    private val collectProgress by JobController { args ->
        var context: Context? = null
        args.forEach {
            if (it is Context) {
                context = it
            }
        }
        context?.let { cont ->
            playAudioUseCase.lastSongProgress(cont).let { lastProgress ->
                playAudioUseCase.songProgression(viewModelScope)
                    .combine(lastProgress) { currentProgress, lastP ->
                        MPLogger.d(
                            CLASS_NAME,
                            "collectProgress",
                            TAG,
                            "currentProgress: $currentProgress, lastP: $lastP"
                        )
                        _uiState.update {
                            it.copy(
                                songProgress = uiState.value.isPlaying.let { isPlaying ->
                                    if (isPlaying) {
                                        if (currentProgress == -1) 0 else currentProgress
                                    } else {
                                        if (lastP == -1) 0 else lastP
                                    }
                                }
                            )
                        }
                    }.collect()
            }
        }
    }

    private val collectRepeatModeJob by JobController { args ->
        var context: Context? = null
        args.forEach {
            if (it is Context) {
                context = it
            }
        }
        context?.let { cont ->
            audioConfiguratorUseCase.getRepeatMode(cont).collectLatest { value: RepeatMode ->
                MPLogger.d(
                    CLASS_NAME, "collectRepeatModeJob",
                    TAG, "repeatMode: $value"
                )
                _uiState.update {
                    it.copy(repeatMode = value)
                }
            }
        }
    }

    private val collectCurrentSongChanges: IJobController by JobController { args ->
        var context: Context? = null
        args.forEach {
            if (it is Context) {
                context = it
            }
        }
        context?.let { ctx ->
            fetchDataUseCase.getSong(ctx, uiState.value.currentSong.id).collectLatest { uiAudio ->
                MPLogger.d(CLASS_NAME, "collectCurrentSongChanges", TAG, "$uiAudio")
                if (uiAudio != null)
                    _uiState.update {
                        it.copy(uiAudio)
                    }
            }
        }
    }

    init {
        handleEvents()

        var stopJob: Job? = null
        stopJob = viewModelScope.launch {
            playAudioUseCase.setOnStopListener(stopJob) { _ ->
                _uiState.update {
                    it.copy(isPlaying = false)
                }
            }
        }
        var playJob: Job? = null
        playJob = viewModelScope.launch {
            playAudioUseCase.setOnPlaySongListener(
                onPlaySongSuccess = { uiAudio ->
                    _uiState.update {
                        it.copy(currentSong = uiAudio, isPlaying = true)
                    }
                },
                onPlaySongFailed = { uiAudio ->
                    _uiState.update {
                        it.copy(currentSong = uiAudio, isPlaying = false)
                    }
                },
                job = playJob
            )
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

    private fun handleEvents() {
        viewModelScope.launch {
            uiEvent.collectLatest { event ->
                MPLogger.d(CLASS_NAME, "handleEvents", TAG, "event: $event")
                when (event) {
                    is TrackDetailsUiEvent.SearchForCurrentSong -> {
                        handleSearchForCurrentSongEvent(event)
                    }

                    is TrackDetailsUiEvent.PauseOrResume -> {
                        handlePauseOrResumeEvent(event)
                    }

                    is TrackDetailsUiEvent.PlayNextSong -> {
                        handlePlayNextSongEvent(event)
                    }

                    is TrackDetailsUiEvent.PlayPreviousSong -> {
                        handlePlayPreviousSongEvent(event)
                    }

                    is TrackDetailsUiEvent.ChangePlayNextBehavior -> {
                        handleChangePlayNextBehaviorEvent(event)
                    }

                    is TrackDetailsUiEvent.ChangeRepeatMode -> {
                        handleChangeRepeatModeEvent(event)
                    }

                    is TrackDetailsUiEvent.UpdatePlayingPosition -> {
                        handleUpdatePlayingPositionEvent(event)
                    }

                    is TrackDetailsUiEvent.Forward -> {
                        handleForwardEvent(event)
                    }

                    is TrackDetailsUiEvent.Rewind -> {
                        handleRewindEvent(event)
                    }

                    is TrackDetailsUiEvent.ChangeFavoriteStatus -> {
                        handleChangeFavoriteStatusEvent(event)
                    }
                }
            }
        }
    }

    private fun handleChangeFavoriteStatusEvent(event: TrackDetailsUiEvent.ChangeFavoriteStatus) {
        MPLogger.d(
            CLASS_NAME,
            "handleChangeFavoriteStatusEvent",
            TAG,
            "current song is favorite: ${uiState.value.currentSong.isFavorite}"
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                editSongUseCase.changeIsFavoriteStatus(
                    event.context,
                    songId = uiState.value.currentSong.id,
                    isFavorite = !uiState.value.currentSong.isFavorite
                )
            }
        }
    }

    private fun handleRewindEvent(event: TrackDetailsUiEvent.Rewind) {
        MPLogger.d(CLASS_NAME, "handleRewindEvent", TAG, "rewindAt: ${event.rewindTo}")
        audioForwardOrRewindUseCase.rewind(rewindAt = event.rewindTo)
    }

    private fun handleForwardEvent(event: TrackDetailsUiEvent.Forward) {
        MPLogger.d(CLASS_NAME, "handleForwardEvent", TAG, "forwardTo: ${event.forwardTo}")
        audioForwardOrRewindUseCase.forward(forwardAt = event.forwardTo)
    }

    private fun handleUpdatePlayingPositionEvent(event: TrackDetailsUiEvent.UpdatePlayingPosition) {
        MPLogger.i(
            CLASS_NAME,
            "handleUpdatePlayingPositionEvent",
            TAG,
            "position: ${event.position}"
        )
        audioForwardOrRewindUseCase.setPlayingPosition(
            context = event.context,
            uri = uiState.value.currentSong.uri,
            position = event.position
        )
    }

    private fun handleChangeRepeatModeEvent(event: TrackDetailsUiEvent.ChangeRepeatMode) {
        MPLogger.i(
            CLASS_NAME,
            "handleChangeRepeatModeEvent",
            TAG,
            "currentRepeatMode: ${uiState.value.repeatMode}"
        )
        viewModelScope.launch {
            audioConfiguratorUseCase.changeRepeatMode(event.context, getNewRepeatMode())
        }
    }

    private fun getNewRepeatMode(): RepeatMode {
        return when (uiState.value.repeatMode) {
            RepeatMode.NO_REPEAT -> RepeatMode.ONE_REPEAT
            RepeatMode.ONE_REPEAT -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.NO_REPEAT
        }
    }

    private fun handleChangePlayNextBehaviorEvent(event: TrackDetailsUiEvent.ChangePlayNextBehavior) {
        MPLogger.i(
            CLASS_NAME,
            "handleChangePlayNextBehaviorEvent",
            TAG,
            "current value of  isRandom: ${uiState.value.isInRandomMode}"
        )
        viewModelScope.launch {
            audioConfiguratorUseCase.changePlayNextOrPreviousMode(
                event.context,
                !uiState.value.isInRandomMode
            )
        }
    }

    private fun handlePlayPreviousSongEvent(event: TrackDetailsUiEvent.PlayPreviousSong) {
        MPLogger.i(CLASS_NAME, "handlePlayPreviousSongEvent", TAG, "try to play next song")
        playNextOrPreviousSongUseCase.playPrevious(
            currentSong = uiState.value.currentSong,
            isRandom = uiState.value.isInRandomMode,
            context = event.context
        )
    }

    private fun handlePlayNextSongEvent(event: TrackDetailsUiEvent.PlayNextSong) {
        MPLogger.i(CLASS_NAME, "handlePlayNextSongEvent", TAG, "try to play previous song")
        playNextOrPreviousSongUseCase.playNext(
            currentSong = uiState.value.currentSong,
            isRandom = uiState.value.isInRandomMode,
            context = event.context
        )
    }

    private fun handlePauseOrResumeEvent(event: TrackDetailsUiEvent.PauseOrResume) {
        MPLogger.i(CLASS_NAME, "handlePauseOrResumeEvent", TAG, "uiAudio: ${event.uiAudio}")
        with(uiState.value) {
            if (isPlaying) {
                playAudioUseCase.currentPlayingSong()?.let {
                    if (it.id == currentSong.id) {
                        MPLogger.d(
                            CLASS_NAME, "handlePlayOrPauseEvent",
                            TAG, "pause: $it"
                        )
                        pauseOrResumeUseCase.pauseSong(event.context, it)
                    } else {
                        MPLogger.w(
                            CLASS_NAME, "handlePlayOrPauseEvent",
                            TAG, "current selected item is not the same as the playing song"
                        )
                    }
                } ?: run {
                    MPLogger.w(
                        CLASS_NAME, "handlePlayOrPauseEvent",
                        TAG, "There no playing song to play"
                    )
                }
            } else {
                playAudioUseCase.currentPlayingSong()?.let {
                    if (it.id == currentSong.id) {
                        MPLogger.d(
                            CLASS_NAME, "handlePlayOrPauseEvent",
                            TAG, "resume: $it"
                        )
                        pauseOrResumeUseCase.resumeSong(event.context, it)
                    } else {
                        MPLogger.d(
                            CLASS_NAME,
                            "handlePlayOrPauseEvent",
                            TAG,
                            "the selected song is not the same zs th current song resume: $currentSong"
                        )
                        playAudioUseCase.playSong(event.context, currentSong)
                    }
                } ?: run {
                    MPLogger.d(
                        CLASS_NAME, "handlePlayOrPauseEvent",
                        TAG, "There no current song so play the selected song play: $currentSong"
                    )
                    playAudioUseCase.playSong(event.context, currentSong)
                }
            }
        }
    }

    private fun handleSearchForCurrentSongEvent(event: TrackDetailsUiEvent.SearchForCurrentSong) {
        MPLogger.i(CLASS_NAME, "handleSearchForCurrentSongEvent", TAG, "songId: ${event.songId}")
        collectProgress.launchJob(event.context)
        collectIsRandomJob.launchJob(event.context)
        collectRepeatModeJob.launchJob(event.context)
        fetchDataUseCase.getExtractedSongList().find { it.id == event.songId }?.let { uiAudio ->
            _uiState.update {
                it.copy(
                    currentSong = uiAudio,
                    isPlaying = playAudioUseCase.isPlaying,
                    songProgress = playAudioUseCase.currentSongProgression
                )
            }
        }
        collectCurrentSongChanges.launchJob(event.context)
    }

    override fun clear() {
        MPLogger.i(CLASS_NAME,"clear", TAG,"clear Jobs")
        collectProgress.cancelJob()
        collectIsRandomJob.cancelJob()
        collectRepeatModeJob.cancelJob()
        collectCurrentSongChanges.cancelJob()
    }

    companion object {
        private const val CLASS_NAME = "TrackDetailVieModel"
        private const val TAG = "DETAIL"
    }
}