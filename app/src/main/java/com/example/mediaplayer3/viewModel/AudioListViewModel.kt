package com.example.mediaplayer3.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.AudioConfigurationUseCase
import com.example.mediaplayer3.domain.AudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.AudioPlayUseCase
import com.example.mediaplayer3.domain.IAudioConfiguratorUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IAudioPlayUseCase
import com.example.mediaplayer3.domain.ISongExtractorUseCase
import com.example.mediaplayer3.domain.SongExtractorUseCase
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.viewModel.data.TrackListUiState
import com.example.mediaplayer3.viewModel.data.UiEvent
import com.example.mediaplayer3.viewModel.delegates.IJobController
import com.example.mediaplayer3.viewModel.delegates.JobController
import com.example.mpdataprovider.contentprovider.data.MissingPermissionException
import com.example.mplog.MPLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias audioList = List<UiAudio>

class AudioListViewModel(
    private val songExtractorUseCase: ISongExtractorUseCase = SongExtractorUseCase,
    private val audioPlayUseCase: IAudioPlayUseCase = AudioPlayUseCase,
    private val audioPauseOrResumeUseCase: IAudioPauseOrResumeUseCase = AudioPauseOrResumeUseCase,
    private val audioConfiguratorUseCase: IAudioConfiguratorUseCase = AudioConfigurationUseCase
) :
    BaseViewModel<UiEvent, TrackListUiState>() {

    private val _uiState = MutableStateFlow(TrackListUiState())
    override val uiState: StateFlow<TrackListUiState> = _uiState.asStateFlow()

    private val randomCollectJob: IJobController by JobController{ list->
        viewModelScope.launch {
            var context: Context? = null
            list.forEach { arg->
                if (arg is Context){
                    context = arg
                }
            }
            context?.let {
                audioConfiguratorUseCase.isRandomModeInFlow(it).collectLatest {isRandomMode->
                    MPLogger.d(
                        CLASS_NAME,"randomCollectJob",
                        TAG,"isRandomMode: $isRandomMode")
                    _uiState.update {trackDetailUiState ->
                        trackDetailUiState.copy(isInRandomMode = isRandomMode)
                    }
                }
            }
        }
    }

    init {
        val repo = getAudioRepo(viewModelScope)
        (songExtractorUseCase as SongExtractorUseCase).invoke(repo)
        (audioPlayUseCase as AudioPlayUseCase).invoke(repo, viewModelScope)
        (audioPauseOrResumeUseCase as AudioPauseOrResumeUseCase).invoke(repo)
        (audioConfiguratorUseCase as AudioConfigurationUseCase).invoke(repo,viewModelScope)
        handleEvent()
        audioPlayUseCase.setOnSongPlayResultListener(
            onSongPlaySuccess = { uiAudio ->
                _uiState.update {
                    it.copy(currentSelectedItem = uiAudio, isPlaying = true)
                }
            },
            onSongPlayFailed = { uiAudio ->
                _uiState.update {
                    it.copy(currentSelectedItem = uiAudio, isPlaying = false)
                }
            }
        )
        audioPlayUseCase.setOnSongStopPlayListener { uiAudio ->
            _uiState.update {
                it.copy(currentSelectedItem = uiAudio, isPlaying = false)
            }
        }
        audioPauseOrResumeUseCase.setOnAudioPauseListener {
            _uiState.update {
                it.copy(isPlaying = false)
            }
        }
        audioPauseOrResumeUseCase.setOnAudioResumeListener(
            onAudioResumeFailed = { _, _ ->
                _uiState.update {
                    it.copy(isPlaying = false)
                }
            },
            onAudioResumeSuccess = { _, _ ->
                _uiState.update {
                    it.copy(isPlaying = true)
                }
            }
        )
    }

    private fun handleEvent() {
        viewModelScope.launch {
            uiEvent.collectLatest {
                MPLogger.d(CLASS_NAME, "handleEvent", TAG, "event: $it")
                when (it) {
                    is UiEvent.FetchData -> {
                        handleFetchDataEvent(it)
                    }

                    is UiEvent.NotifyPermissionNeeded -> {
                        handleNotifyPermissionNeeded()
                    }

                    is UiEvent.PlaySong -> {
                        handlePlaySongEvent(it)
                    }

                    is UiEvent.PauseOrResume -> {
                        handlePauseOrResumeEvent(it)
                    }

                    is UiEvent.PlayNextSong -> {
                        handlePlayNextSongEvent(it)
                    }

                    is UiEvent.PlayPreviousSong -> {
                        handlePlayPreviousSongEvent(it)
                    }
                }
            }
        }
    }

    private fun handlePlayPreviousSongEvent(uiEvent: UiEvent.PlayPreviousSong) {
        MPLogger.d(CLASS_NAME, "handlePlayPreviousSongEvent", TAG, "trying to play previous song")
        with(uiState.value) {
            if (!isInRandomMode){
                dataList.find { it.id == currentSelectedItem?.id }?.let {
                    val index = dataList.indexOf(it)
                    val previousSong =
                        dataList[if (index - 1 >= 0) index - 1 else dataList.size - 1]
                    MPLogger.d(
                        CLASS_NAME,
                        "handlePlayPreviousSongEvent",
                        TAG,
                        "play previous song $previousSong"
                    )
                    audioPlayUseCase.playSong(uiEvent.context, previousSong)
                }
            }else{
                val previousSong = dataList.random()
                MPLogger.d(
                    CLASS_NAME,
                    "handlePlayPreviousSongEvent",
                    TAG,
                    "play previous song $previousSong"
                )
                audioPlayUseCase.playSong(uiEvent.context, previousSong)
            }
        }
    }

    private fun handlePlayNextSongEvent(uiEvent: UiEvent.PlayNextSong) {
        MPLogger.d(CLASS_NAME, "handlePlayNextSongEvent", TAG, "trying to play next song")
        with(uiState.value) {
            if (!isInRandomMode){
                dataList.find { it.id == currentSelectedItem?.id }?.let {
                    val index = dataList.indexOf(it)
                    val nextSong = dataList[if (index + 1 < dataList.size) index + 1 else 0]
                    MPLogger.d(
                        CLASS_NAME,
                        "handlePlayNextSongEvent",
                        TAG,
                        "play next song $nextSong"
                    )
                    audioPlayUseCase.playSong(uiEvent.context, nextSong)
                }
            }else{
                val nextSong = dataList.random()
                MPLogger.d(
                    CLASS_NAME,
                    "handlePlayNextSongEvent",
                    TAG,
                    "play next song $nextSong"
                )
                audioPlayUseCase.playSong(uiEvent.context, nextSong)
            }
        }
    }

    private fun handlePauseOrResumeEvent(uiEvent: UiEvent.PauseOrResume) {
        MPLogger.d(CLASS_NAME, "handlePauseOrResumeEvent", TAG, "uiAudio ${uiEvent.uiAudio}")
        if (uiState.value.isPlaying) {
            MPLogger.d(
                CLASS_NAME,
                "handlePauseOrResumeEvent",
                TAG,
                "pause uiAudio ${uiEvent.uiAudio}"
            )
            audioPauseOrResumeUseCase.pauseSong(uiEvent.context, uiEvent.uiAudio)
        } else {
            MPLogger.d(
                CLASS_NAME,
                "handlePauseOrResumeEvent",
                TAG,
                "resume uiAudio ${uiEvent.uiAudio}"
            )
            checkLastPayingSongDuration(uiEvent.context) {
                audioPauseOrResumeUseCase.resumeSong(
                    context = uiEvent.context,
                    uiAudio = uiEvent.uiAudio,
                    seekTo = it
                )
            }
        }
    }

    private fun handlePlaySongEvent(uiEvent: UiEvent.PlaySong) {
        MPLogger.d(CLASS_NAME, "handlePlaySongEvent", TAG, "play song ${uiEvent.uiAudio}")
        lastPlayingSongJob.cancelJob()
        //Play at when user move the Progress bar of the slider in the track detail screen the playAt means the progress value
        // so the songs will be played at that position if the song not already Playing
        if (uiEvent.playAt != -1) {
            MPLogger.d(CLASS_NAME, "handlePlaySongEvent", TAG, "startAt: ${uiEvent.playAt}")
            audioPlayUseCase.playSong(
                context = uiEvent.context,
                uiAudio = uiEvent.uiAudio,
                seekTo = uiEvent.playAt
            )
            return
        }
        checkLastPayingSongDuration(uiEvent.context) {
            audioPlayUseCase.playSong(
                context = uiEvent.context,
                uiAudio = uiEvent.uiAudio,
                seekTo = it
            )
        }
    }

    private fun checkLastPayingSongDuration(context: Context, playWitDuration: (Int) -> Unit) {
        var job: Job? = null
        var isProgressDeterminate = false
        job = viewModelScope.launch {
            audioPlayUseCase.getLastPlayingSongDuration(context).collectLatest {
                MPLogger.d(
                    CLASS_NAME,
                    "checkLastPayingSongDuration",
                    TAG,
                    "lastPlayingSong Duration: $it"
                )
                if (!isProgressDeterminate) {
                    if (it != -1) {
                        isProgressDeterminate = true
                    }
                    playWitDuration(it)
                    job?.cancel()
                } else {
                    job?.cancel()
                }
            }
        }
    }

    private fun handleNotifyPermissionNeeded() {
        MPLogger.d(CLASS_NAME, "handleNotifyPermissionNeeded", TAG, "permission denied")
        lastPlayingSongJob.cancelJob()
        _uiState.update {
            it.copy(
                isPermissionGranted = false,
                needShowDialogForPermission = true,
                isLoading = false,
                error = MissingPermissionException(
                    emptyList()
                )
            )
        }
    }

    private val lastPlayingSongJob by JobController { args ->
        val context = args.first() as Context
        audioPlayUseCase.getLastPlayingSong(context).stateIn(viewModelScope)
            .collectLatest { id ->
                MPLogger.d(CLASS_NAME, "lastPlayingSongJob", TAG, "last playing song id $id")

                val result = args[1] as audioList
                val currentSelectedSong = result.firstOrNull { uiAudio -> uiAudio.id == id }
                currentSelectedSong?.let { uiAudio ->
                    MPLogger.d(
                        CLASS_NAME,
                        "lastPlayingSongJob",
                        TAG,
                        "currentSelectedItem $uiAudio"
                    )
                    _uiState.update {
                        it.copy(
                            dataList = result,
                            isLoading = false,
                            currentSelectedItem = uiAudio
                        )
                    }
                } ?: kotlin.run {
                    MPLogger.d(
                        CLASS_NAME,
                        "lastPlayingSongJob",
                        TAG,
                        "currentSelectedItem ${result.firstOrNull()}"
                    )
                    _uiState.update {
                        it.copy(
                            dataList = result,
                            isLoading = false,
                            currentSelectedItem = result.firstOrNull()
                        )
                    }
                }
            }
    }

    private fun handleFetchDataEvent(uiEvent: UiEvent.FetchData) {
        randomCollectJob.launchJob(uiEvent.context)
        if (songExtractorUseCase.getExtractedSongList().isNotEmpty()) {
            MPLogger.i(
                CLASS_NAME,
                "handleFetchDataEvent",
                TAG,
                "the song list is already cached so load the cached instead of fetching the date"
            )
            viewModelScope.launch {
                lastPlayingSongJob.launchJob(
                    uiEvent.context,
                    songExtractorUseCase.getExtractedSongList()
                )
            }
            return
        }
        _uiState.update {
            it.copy(isPermissionGranted = true, isLoading = true)
        }
        viewModelScope.launch {
            when (val result = songExtractorUseCase.getSongs(uiEvent.context)) {
                is Result.Success -> {
                    MPLogger.d(
                        CLASS_NAME,
                        "handleFetchDataEvent",
                        TAG,
                        "songs fetched successfully ${result.data.size}"
                    )
                    lastPlayingSongJob.launchJob(uiEvent.context, result.data)
                }

                is Result.Error -> {
                    MPLogger.d(
                        CLASS_NAME,
                        "handleFetchDataEvent",
                        TAG,
                        "songs not fetched successfully ${result.t.message}"
                    )
                    if (result.t is MissingPermissionException) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.t,
                                needShowDialogForPermission = true,
                                isPermissionGranted = false
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        randomCollectJob.cancelJob()
    }

    companion object {
        private const val CLASS_NAME = "AudioViewModel"
        private const val TAG = "AUDIO"
    }
}