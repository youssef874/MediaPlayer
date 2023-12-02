package com.example.mediaplayer3.viewModel

import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.domain.FetchDataUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.ResumePauseSongUseCaseImpl
import com.example.mediaplayer3.domain.entity.PlayAudioUseCase
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailUiState
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailsUiEvent
import com.example.mediaplayer3.viewModel.delegates.INextOrPreviousItem
import com.example.mediaplayer3.viewModel.delegates.NextOrPreviousDelegate
import com.example.mplog.MPLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackDetailViewModel(
    private val fetchDataUseCase: IFetchDataUseCase = FetchDataUseCase,
    private val playAudioUseCase: IPlayAudioUseCase = PlayAudioUseCase,
    private val pauseOrResumeUseCase: IAudioPauseOrResumeUseCase = ResumePauseSongUseCaseImpl
) : BaseViewModel<TrackDetailsUiEvent,TrackDetailUiState>() {

    private val _uiState = MutableStateFlow(TrackDetailUiState())
    override val uiState: StateFlow<TrackDetailUiState>  = _uiState.asStateFlow()

    private val nextOrPreviousDelegate: INextOrPreviousItem<UiAudio> by NextOrPreviousDelegate()
    init {
        val repo = getAudioDataRepo()
        (fetchDataUseCase as FetchDataUseCase).invoke(repo,viewModelScope)
        (playAudioUseCase as PlayAudioUseCase).invoke(repo,fetchDataUseCase)
        (pauseOrResumeUseCase as ResumePauseSongUseCaseImpl).invoke(repo,playAudioUseCase)
        handleEvents()

        var stopJob: Job? = null
        stopJob = viewModelScope.launch {
            playAudioUseCase.setOnStopListener(stopJob){_ ->
                _uiState.update {
                    it.copy(isPlaying = false)
                }
            }
        }
        var playJob: Job? = null
        playJob = viewModelScope.launch {
            playAudioUseCase.setOnPlaySongListener(
                onPlaySongSuccess = {uiAudio ->
                    _uiState.update {
                        it.copy(currentSong = uiAudio, isPlaying = true)
                    }
                },
                onPlaySongFailed = {uiAudio ->
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
            uiEvent.collectLatest { event->
                MPLogger.d(CLASS_NAME,"handleEvents", TAG,"event: $event")
                when(event){
                    is TrackDetailsUiEvent.SearchForCurrentSong->{
                        handleSearchForCurrentSongEvent(event)
                    }
                    is TrackDetailsUiEvent.PauseOrResume->{
                        handlePauseOrResumeEvent(event)
                    }
                    is TrackDetailsUiEvent.PlayNextSong->{
                        handlePlayNextSongEvent(event)
                    }
                    is TrackDetailsUiEvent.PlayPreviousSong->{
                        handlePlayPreviousSongEvent(event)
                    }
                    else->Unit
                }
            }
        }
    }

    private fun handlePlayPreviousSongEvent(event: TrackDetailsUiEvent.PlayPreviousSong) {
        MPLogger.i(CLASS_NAME,"handlePlayPreviousSongEvent", TAG,"try to play next song")
        nextOrPreviousDelegate.previousItem(
            list = fetchDataUseCase.getExtractedSongList(),
            currentItem = uiState.value.currentSong
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

    private fun handlePlayNextSongEvent(event: TrackDetailsUiEvent.PlayNextSong) {
        MPLogger.i(CLASS_NAME,"handlePlayNextSongEvent", TAG,"try to play previous song")
        nextOrPreviousDelegate.nextItem(
            list = fetchDataUseCase.getExtractedSongList(),
            currentItem = uiState.value.currentSong
        ){next: UiAudio ->
            MPLogger.i(
                CLASS_NAME, "handlePlayNextSongEvent",
                TAG, "play next Song: $next")
            playAudioUseCase.playSong(event.context, next, 0)
        }
    }

    private fun handlePauseOrResumeEvent(event: TrackDetailsUiEvent.PauseOrResume) {
        MPLogger.i(CLASS_NAME,"handlePauseOrResumeEvent", TAG,"uiAudio: ${event.uiAudio}")
        with(uiState.value){
            if (isPlaying){
                playAudioUseCase.currentPlayingSong()?.let {
                    if (it.id == currentSong.id){
                        MPLogger.d(
                            CLASS_NAME,"handlePlayOrPauseEvent",
                            TAG,"pause: $it")
                        pauseOrResumeUseCase.pauseSong(event.context,it)
                    }else{
                        MPLogger.w(
                            CLASS_NAME,"handlePlayOrPauseEvent",
                            TAG,"current selected item is not the same as the playing song")
                    }
                }?:run {
                    MPLogger.w(
                        CLASS_NAME,"handlePlayOrPauseEvent",
                        TAG,"There no playing song to play")
                }
            }else{
                playAudioUseCase.currentPlayingSong()?.let {
                    if (it.id == currentSong.id){
                        MPLogger.d(
                            CLASS_NAME,"handlePlayOrPauseEvent",
                            TAG,"resume: $it")
                        pauseOrResumeUseCase.resumeSong(event.context,it)
                    }else{
                        MPLogger.d(
                            CLASS_NAME,"handlePlayOrPauseEvent",
                            TAG,"the selected song is not the same zs th current song resume: $currentSong")
                        playAudioUseCase.playSong(event.context, currentSong)
                    }
                }?:run {
                    MPLogger.d(
                        CLASS_NAME,"handlePlayOrPauseEvent",
                        TAG,"There no current song so play the selected song play: $currentSong")
                    playAudioUseCase.playSong(event.context, currentSong)
                }
            }
        }
    }

    private fun handleSearchForCurrentSongEvent(event: TrackDetailsUiEvent.SearchForCurrentSong) {
        MPLogger.i(CLASS_NAME,"handleSearchForCurrentSongEvent", TAG,"songId: ${event.songId}")
        fetchDataUseCase.getExtractedSongList().find { it.id == event.songId }?.let { uiAudio->
            _uiState.update {
                it.copy(currentSong = uiAudio, isPlaying = playAudioUseCase.isPlaying)
            }
        }
    }

    companion object{
        private const val CLASS_NAME = "TrackDetailVieModel"
        private const val TAG = "DETAIL"
    }
}