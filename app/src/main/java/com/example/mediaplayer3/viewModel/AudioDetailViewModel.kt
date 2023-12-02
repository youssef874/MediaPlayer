package com.example.mediaplayer3.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.domain.AudioConfigurationUseCase
import com.example.mediaplayer3.domain.AudioForwardOrRewindUseCase
import com.example.mediaplayer3.domain.AudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.AudioPlayUseCase
import com.example.mediaplayer3.domain.FetchDataUseCase
import com.example.mediaplayer3.domain.IAudioConfiguratorUseCase
import com.example.mediaplayer3.domain.IAudioForwardOrRewindUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IAudioPlayUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.ISongEditUseCase
import com.example.mediaplayer3.domain.SongEditUseCase
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailUiState
import com.example.mediaplayer3.viewModel.data.trackDetail.TrackDetailsUiEvent
import com.example.mediaplayer3.viewModel.delegates.AudioProgressJob
import com.example.mediaplayer3.viewModel.delegates.IJobController
import com.example.mediaplayer3.viewModel.delegates.JobController
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mplog.MPLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AudioDetailViewModel(
    private val fetchDataUseCase: IFetchDataUseCase = FetchDataUseCase,
    private val audioPlayUseCase: IAudioPlayUseCase = AudioPlayUseCase,
    private val audioPauseOrResumeUseCase: IAudioPauseOrResumeUseCase = AudioPauseOrResumeUseCase,
    private val audioForwardOrRewindUseCase: IAudioForwardOrRewindUseCase = AudioForwardOrRewindUseCase,
    private val audioConfiguratorUseCase: IAudioConfiguratorUseCase = AudioConfigurationUseCase,
    private val songEditUseCase: ISongEditUseCase = SongEditUseCase
): BaseViewModel<TrackDetailsUiEvent, TrackDetailUiState>() {

    private val _uiState = MutableStateFlow(TrackDetailUiState())
    override val uiState: StateFlow<TrackDetailUiState> = _uiState.asStateFlow()

    private val audioProgress by AudioProgressJob { context ->
        audioPlayUseCase.getPlayingSongDuration(context).collectLatest { progression ->
            MPLogger.d(CLASS_NAME,"audioProgress", TAG,"progression: $progression")
            if (progression != -1) {
                _uiState.update {
                    it.copy(songProgress = progression)
                }
            }
        }
    }

    private val randomCollectJob: IJobController by JobController{list->
        viewModelScope.launch {
            var context: Context? = null
            list.forEach { arg->
                if (arg is Context){
                    context = arg
                }
            }
            context?.let {
                audioConfiguratorUseCase.isRandomModeInFlow(it).collectLatest {isRandomMode->
                    MPLogger.d(CLASS_NAME,"randomCollectJob", TAG,"isRandomMode: $isRandomMode")
                    _uiState.update {trackDetailUiState ->
                        trackDetailUiState.copy(isInRandomMode = isRandomMode)
                    }
                }
            }
        }
    }

    private val repeatModeCollectJob: IJobController by JobController{ list->
        viewModelScope.launch {
            var context: Context? = null
            list.forEach { arg->
                if (arg is Context){
                    context = arg
                }
            }
            context?.let {cont->
                audioConfiguratorUseCase.getRepeatMode(cont).collectLatest { repeatMode->
                    MPLogger.d(CLASS_NAME,"repeatModeCollectJob", TAG,"repeatMode: $repeatMode")
                    /*
                    _uiState.update {
                        it.copy(repeatMode = repeatMode)
                    }
                    */
                }
            }
        }
    }
    
    init {
        val repo = getAudioRepo(viewModelScope)
        val audioDataRepo = getAudioDataRepo()
        (fetchDataUseCase as FetchDataUseCase).invoke(audioDataRepo,viewModelScope)
        (audioPlayUseCase as AudioPlayUseCase).invoke(repo,viewModelScope)
        (audioPauseOrResumeUseCase as AudioPauseOrResumeUseCase).invoke(repo)
        (audioForwardOrRewindUseCase as AudioForwardOrRewindUseCase).invoke(repo)
        (audioConfiguratorUseCase as AudioConfigurationUseCase).invoke(repo,viewModelScope)
        (songEditUseCase as SongEditUseCase).invoke(repo)
        handleEvents()
        audioPlayUseCase.setOnSongPlayResultListener(
            onSongPlaySuccess = {uiAudio->
                MPLogger.d(CLASS_NAME,"onSongPlaySuccess", TAG,"uiAudio: $uiAudio")
                _uiState.update {
                    it.copy(currentSong = uiAudio, isPlaying = true)
                }
            },
            onSongPlayFailed = {uiAudio->
                MPLogger.d(CLASS_NAME,"onSongPlayFailed", TAG,"uiAudio: $uiAudio")
                _uiState.update {
                    it.copy(currentSong = uiAudio, isPlaying = false)
                }
            }
        )
        audioPlayUseCase.setOnSongStopPlayListener { uiAudio ->
            MPLogger.d(CLASS_NAME,"onSongStopPlay", TAG,"uiAudio: $uiAudio")
            _uiState.update {
                it.copy(isPlaying = false)
            }
        }
        audioPauseOrResumeUseCase.setOnAudioPauseListener {uiAudio ->
            MPLogger.d(CLASS_NAME,"onAudioPaused", TAG,"uiAudio: $uiAudio")
            _uiState.update {
                it.copy(isPlaying = false)
            }
            audioProgress.cancelJob()
        }
        audioPauseOrResumeUseCase.setOnAudioResumeListener(
            onAudioResumeSuccess = {uiAudio, context ->
                MPLogger.d(CLASS_NAME,"onAudioResumeSuccess", TAG,"uiAudio: $uiAudio")
                _uiState.update {
                    it.copy(isPlaying = true)
                }
                audioProgress.launchJob(context)
            },
            onAudioResumeFailed = {uiAudio, _ ->
                MPLogger.d(CLASS_NAME,"onAudioResumeFailed", TAG,"uiAudio: $uiAudio")
                _uiState.update {
                    it.copy(isPlaying = false)
                }
                audioProgress.cancelJob()
            }
        )
        songEditUseCase.onFavoriteChangesForAudio { uiAudio ->
            MPLogger.d(CLASS_NAME,"onFavoriteChange", TAG,"uiAudio: $uiAudio")
            _uiState.update {
                it.copy(currentSong = uiAudio)
            }
        }
    }
    
    private fun handleEvents(){
        viewModelScope.launch { 
            uiEvent.collectLatest { 
                MPLogger.d(CLASS_NAME,"handleEvents", TAG,"event: $it")
                when(it){
                    is TrackDetailsUiEvent.PauseOrResume->{
                        handlePauseOrResumeEvent(it)
                    }
                    is TrackDetailsUiEvent.SearchForCurrentSong->{
                        handleSearchForCurrentSongEvent(it)
                    }
                    is TrackDetailsUiEvent.PlayNextSong->{
                        handlePlayNextSongEvent(it)
                    }
                    is TrackDetailsUiEvent.PlayPreviousSong->{
                        handlePlayPreviousSongEvent(it)
                    }
                    is TrackDetailsUiEvent.Rewind->{
                        handleRewindEvent(it)
                    }
                    is TrackDetailsUiEvent.Forward->{
                        handleForwardEvent(it)
                    }
                    is TrackDetailsUiEvent.UpdatePlayingPosition->{
                        handleUpdatePlayingPositionEvent(it)
                    }
                    is TrackDetailsUiEvent.ChangePlayNextBehavior->{
                        handleChangePlayNextBehaviorEvent(it)
                    }
                    is TrackDetailsUiEvent.ChangeRepeatMode->{
                        handleChangeRepeatModeEvent(it)
                    }
                    is TrackDetailsUiEvent.ChangeFavoriteStatus->{
                        handleChangeFavoriteStatusEvent(it)
                    }
                }
            }
        }
    }

    private fun handleChangeFavoriteStatusEvent(event: TrackDetailsUiEvent.ChangeFavoriteStatus) {
        MPLogger.i(CLASS_NAME,"handleChangeFavoriteStatusEvent", TAG,"currentFavoriteStatus: ${uiState.value.currentSong.isFavorite}")
        viewModelScope.launch {
            songEditUseCase.changeFavoriteStatus(event.context,uiState.value.currentSong)
        }
    }

    private fun handleChangeRepeatModeEvent(event: TrackDetailsUiEvent.ChangeRepeatMode) {
        MPLogger.i(CLASS_NAME,"handleChangeRepeatModeEvent", TAG,"currentRepeatMode:  ${uiState.value.repeatMode}")
        when(uiState.value.repeatMode){
            RepeatMode.NO_REPEAT->{
                viewModelScope.launch {
                    MPLogger.i(CLASS_NAME,"handleChangeRepeatModeEvent", TAG,"change repeat mode to ONE_REPEAT")
                    //audioConfiguratorUseCase.changeRepeatMode(event.context,RepeatMode.ONE_REPEAT)
                }
            }
            RepeatMode.ONE_REPEAT->{
                viewModelScope.launch {
                    MPLogger.i(CLASS_NAME,"handleChangeRepeatModeEvent", TAG,"change repeat mode to REPEAT_ALL")
                    //audioConfiguratorUseCase.changeRepeatMode(event.context,RepeatMode.REPEAT_ALL)
                }
            }
            RepeatMode.REPEAT_ALL->{
                viewModelScope.launch {
                    MPLogger.i(CLASS_NAME,"handleChangeRepeatModeEvent", TAG,"change repeat mode to NO_REPEAT")
                    //audioConfiguratorUseCase.changeRepeatMode(event.context,RepeatMode.NO_REPEAT)
                }
            }
        }
    }

    private fun handleChangePlayNextBehaviorEvent(event: TrackDetailsUiEvent.ChangePlayNextBehavior) {
        MPLogger.i(CLASS_NAME,"handleChangePlayNextBehaviorEvent", TAG,"currentMode: ${uiState.value.isInRandomMode}")
        audioConfiguratorUseCase.changePlayNextOrPreviousMode(
            context = event.context,
            isRandom = !uiState.value.isInRandomMode
        )
    }

    private fun handleUpdatePlayingPositionEvent(event: TrackDetailsUiEvent.UpdatePlayingPosition) {
        MPLogger.d(CLASS_NAME,"handleUpdatePlayingPositionEvent", TAG,"update player to position: ${event.position}")
            audioForwardOrRewindUseCase.setPlayingPosition(
                context = event.context,
                uri = uiState.value.currentSong.uri,
                position = event.position
            )
    }

    private fun handleForwardEvent(event: TrackDetailsUiEvent.Forward) {
        MPLogger.d(CLASS_NAME,"handleForwardEvent", TAG,"forwardTo: ${event.forwardTo}")
            if (uiState.value.isPlaying){
                MPLogger.d(CLASS_NAME,"handleForwardEvent", TAG,"forward")
                audioForwardOrRewindUseCase.forward(event.forwardTo)
            }else{
                val startAt = uiState.value.songProgress + event.forwardTo
                MPLogger.d(CLASS_NAME,"handleForwardEvent", TAG,"song not playing so start current song at: $startAt")
                audioPlayUseCase.playSong(
                    context = event.context,
                    uiAudio = uiState.value.currentSong,
                    seekTo = startAt
                )
            }
    }

    private fun handleRewindEvent(event: TrackDetailsUiEvent.Rewind) {
        MPLogger.d(CLASS_NAME,"handleRewindEvent", TAG,"rewind: ${event.rewindTo}")
            if (uiState.value.isPlaying){
                MPLogger.d(CLASS_NAME,"handleRewindEvent", TAG,"rewind to: ${event.rewindTo}")
                audioForwardOrRewindUseCase.rewind(event.rewindTo)
            }else{
                val startAt = uiState.value.songProgress - event.rewindTo
                MPLogger.d(CLASS_NAME,"handleRewindEvent", TAG,"song not playing so start current song at: $startAt")
                audioPlayUseCase.playSong(
                    context = event.context,
                    uiAudio = uiState.value.currentSong,
                    seekTo = startAt
                )
            }
    }

    private fun handlePlayPreviousSongEvent(event: TrackDetailsUiEvent.PlayPreviousSong) {
        MPLogger.d(CLASS_NAME,"handlePlayPreviousSongEvent", TAG,"Try to play previous song")
        with(fetchDataUseCase.getExtractedSongList()){
            if (!uiState.value.isInRandomMode){
                find { it.id == uiState.value.currentSong.id }?.let {
                    val index = indexOf(it)
                    val previousSong = get(if (index - 1 >= 0) index - 1 else size - 1)
                    MPLogger.d(CLASS_NAME,"handlePlayPreviousSongEvent", TAG,"play previous song")
                    audioPlayUseCase.playSong(event.context,previousSong)
                }
            }else{
                val previousSong = random()
                MPLogger.d(CLASS_NAME,"handlePlayPreviousSongEvent", TAG,"play previous song")
                audioPlayUseCase.playSong(event.context,previousSong)
            }
        }
    }

    private fun handlePlayNextSongEvent(event: TrackDetailsUiEvent.PlayNextSong) {
        MPLogger.d(CLASS_NAME,"handlePlayNextSongEvent", TAG,"try to play next song")
        with(fetchDataUseCase.getExtractedSongList()){
            if (!uiState.value.isInRandomMode){
                find { it.id == uiState.value.currentSong.id }?.let {
                    val index: Int = indexOf(it)
                    val nextSong = get(if (index + 1 < size) index + 1 else 0)
                    MPLogger.d(CLASS_NAME,"handlePlayNextSongEvent", TAG,"play next song: $nextSong")
                    audioPlayUseCase.playSong(event.context,nextSong)
                }
            }else{
                val nextSong = random()
                MPLogger.d(CLASS_NAME,"handlePlayNextSongEvent", TAG,"play next song: $nextSong")
                audioPlayUseCase.playSong(event.context,nextSong)
            }
        }
    }

    private fun handleSearchForCurrentSongEvent(event: TrackDetailsUiEvent.SearchForCurrentSong) {
        MPLogger.d(CLASS_NAME,"handleSearchForCurrentSongEvent", TAG,"songId: ${event.songId}")
        fetchDataUseCase.getExtractedSongList().find { it.id == event.songId }?.let { uiAudio->
            _uiState.update {
                it.copy(currentSong = uiAudio)
            }
            randomCollectJob.launchJob(event.context)
            audioProgress.launchJob(event.context)
            repeatModeCollectJob.launchJob(event.context)
        }

    }

    private fun handlePauseOrResumeEvent(uiEvent: TrackDetailsUiEvent.PauseOrResume) {
        MPLogger.d(CLASS_NAME,"handlePauseOrResumeEvent", TAG,"uiAudio: ${uiEvent.uiAudio}")
        if (uiState.value.isPlaying){
            MPLogger.d(CLASS_NAME,"handlePauseOrResumeEvent", TAG,"pause uiAudio ${uiEvent.uiAudio}")
            audioPauseOrResumeUseCase.pauseSong(uiEvent.context,uiEvent.uiAudio)
        }else{
            MPLogger.d(CLASS_NAME,"handlePauseOrResumeEvent", TAG,"resume uiAudio ${uiEvent.uiAudio}")
            checkLastPayingSongDuration(uiEvent.context) {
                audioPauseOrResumeUseCase.resumeSong(
                    context = uiEvent.context,
                    uiAudio = uiEvent.uiAudio,
                    seekTo = it
                )
            }
        }
    }

    private fun checkLastPayingSongDuration(context: Context, playWitDuration: (Int) -> Unit) {
        var job: Job? = null
        var isProgressDeterminate = false
        job = viewModelScope.launch {
            audioPlayUseCase.getLastPlayingSongDuration(context).collectLatest {
                MPLogger.d(CLASS_NAME,"checkLastPayingSongDuration", TAG,"lastPlayingSong Duration: $it")
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

    override fun onCleared() {
        super.onCleared()
        randomCollectJob.cancelJob()
        audioProgress.cancelJob()
        repeatModeCollectJob.cancelJob()
    }

    companion object{
        private const val CLASS_NAME = "AudioDetailViewModel"
        private const val TAG = "AUDIO"
    }
}