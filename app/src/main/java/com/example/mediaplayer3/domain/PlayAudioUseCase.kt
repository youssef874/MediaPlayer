package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.RepeatMode
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mpcore.api.log.MPLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val audioDataRepository: IAudioDataRepo,
    private val fetchDataUseCase: IFetchDataUseCase,
    private val audioConfiguratorUseCase: IAudioConfiguratorUseCase
) : IPlayAudioUseCase, IUseCase {

    companion object {
        private const val CLASS_NAME = "PlayAudioUseCase"
        private const val TAG = "AUDIO"
    }

    private var sScope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = sScope

    private val audioPlaySuccessListener = mutableListOf<(UiAudio) -> Unit>()
    private val audioPlayFailedListener = mutableListOf<(UiAudio) -> Unit>()
    private val audioStopListener = mutableListOf<(UiAudio) -> Unit>()

    private var currentSong: UiAudio? = null

    private var _isPlaying = false
    override val isPlaying: Boolean
        get() = _isPlaying

    private var _currentSongProgression: Int = 0
    override val currentSongProgression: Int
        get() = _currentSongProgression

    private var isRandom = false

    private var repeatMode = RepeatMode.NO_REPEAT

    private val progressSongJobScheduler: IUseCaseJobScheduler by UseCaseScheduler { list ->
        var context: Context? = null
        list.forEach { arg ->
            if (arg is Context) {
                context = arg
            }
        }
        songProgression(scope).collectLatest { progress ->
            MPLog.d(CLASS_NAME, "progressSongJobScheduler", TAG, "progress: $progress")
            context?.let {
                audioDataRepository.updateLastSongProgress(it, progress)
            }
        }
    }

    private val collectIsRandomJobScheduler: IUseCaseJobScheduler by UseCaseScheduler { list ->
        var context: Context? = null
        list.forEach { arg ->
            if (arg is Context) {
                context = arg
            }
        }
        context?.let {
            audioConfiguratorUseCase.isRandomModeInFlow(it).collectLatest { _isRandom ->
                MPLog.d(CLASS_NAME, "collectRepeatModeJob", TAG, "_isRandom: $_isRandom")
                isRandom = _isRandom
            }
        }
    }

    private val collectRepeatModeJob: IUseCaseJobScheduler by UseCaseScheduler { args ->
        var context: Context? = null
        args.forEach { arg ->
            if (arg is Context) {
                context = arg
            }
        }
        context?.let {
            audioConfiguratorUseCase.getRepeatMode(it).collectLatest { value: RepeatMode ->
                MPLog.d(CLASS_NAME, "collectRepeatModeJob", TAG, "repeatMode: $value")
                repeatMode = value
            }
        }
    }

    private val collectLastProgressionJob: IUseCaseJobScheduler by UseCaseScheduler { args ->
        var context: Context? = null
        args.forEach { arg ->
            if (arg is Context) {
                context = arg
            }
        }
        context?.let {
            audioDataRepository.observeLastSongProgression(it).stateIn(scope)
                .collectLatest { progression ->
                    _currentSongProgression = progression
                }
        }
    }


    override fun playSong(
        context: Context,
        uiAudio: UiAudio,
        seekTo: Int
    ) {
        MPLog.d(CLASS_NAME, "playSong", TAG, "try to play: $uiAudio")
        collectIsRandomJobScheduler.launchJob(context)
        collectRepeatModeJob.launchJob(context)
        try {
            if (seekTo != -1) {
                MPLog.d(CLASS_NAME, "playSong", TAG, "play: $uiAudio, at: $seekTo")
                scope.launch {
                    audioDataRepository.updateLastSongProgress(context, seekTo)
                }
                currentSong?.let {
                    audioDataRepository.stopSong(context, it.uri)
                }
                audioDataRepository.playSong(context, uiAudio.uri, seekTo)
                _isPlaying = true
            } else {
                var job: Job? = null
                currentSong?.let {
                    audioDataRepository.stopSong(context, it.uri)
                }
                job = scope.launch {
                    lastSongProgress(context).collectLatest {
                        MPLog.d(CLASS_NAME, "playSong", TAG, "lastSongProgress: $it")
                        if (it != -1) {
                            audioDataRepository.playSong(context, uiAudio.uri, it)
                        } else {
                            audioDataRepository.playSong(context, uiAudio.uri)
                        }
                        job?.cancel()
                    }
                }
            }
            progressSongJobScheduler.launchJob(context)
            scope.launch {
                withContext(Dispatchers.IO) {
                    audioDataRepository.updateLastPlayingSong(context, uiAudio.id)
                }
            }
            audioDataRepository.observeSongCompletion {
                MPLog.d(CLASS_NAME, "onSongCompleted", TAG, "current playing song completed")
                val audioList = fetchDataUseCase.getExtractedSongList()
                val currentSongIndex = audioList.indexOf(uiAudio)
                val nextSongIndex = getNextSongIndex(currentSongIndex, audioList)
                scope.launch {
                    audioDataRepository.updateLastSongProgress(context,0)
                }
                val nextSong = audioList[nextSongIndex]
                MPLog.d(CLASS_NAME, "onSongCompleted", TAG, "nextSong: $nextSong")
                playSong(context, nextSong)
            }
            _isPlaying = true
            currentSong = uiAudio
            audioPlaySuccessListener.forEach {
                it(uiAudio)
            }
        } catch (_: Exception) {
            MPLog.w(CLASS_NAME, "playSong", TAG, "Failed to play $uiAudio")
            _isPlaying = false
            audioPlayFailedListener.forEach {
                it(uiAudio)
            }
        }
    }

    private fun getNextSongIndex(
        currentSongIndex: Int,
        audioList: List<UiAudio>
    ) = when (repeatMode) {
        RepeatMode.NO_REPEAT -> {
            if (!isRandom) {
                if (currentSongIndex + 1 <= audioList.size - 1) currentSongIndex + 1 else audioList.size - 1
            } else {
                audioList.indices.random()
            }
        }

        RepeatMode.ONE_REPEAT -> {
            if (!isRandom) {
                if (currentSongIndex + 1 <= audioList.size - 1) currentSongIndex + 1 else 0
            } else {
                audioList.indices.random()
            }
        }

        RepeatMode.REPEAT_ALL -> {
            currentSongIndex
        }
    }

    override fun stopSong(context: Context) {
        MPLog.i(CLASS_NAME, "stopSong", TAG, "try to stop song")
        currentSong?.let { uiAudio ->
            MPLog.i(CLASS_NAME, "stopSong", TAG, "stop playing: $uiAudio")
            audioDataRepository.stopSong(context, uiAudio.uri)
            progressSongJobScheduler.cancelJob()
            audioStopListener.forEach {
                it(uiAudio)
            }
            _isPlaying = false
            currentSong = null
        } ?: run {
            MPLog.w(CLASS_NAME, "stopSong", TAG, "There no song was playing to stop")
        }
    }

    override fun updatePlyingStatus(isPlaying: Boolean) {
        MPLog.i(CLASS_NAME, "updatePlyingStatus", TAG, "isPlaying: $isPlaying")
        _isPlaying = isPlaying
    }

    override suspend fun setOnPlaySongListener(
        onPlaySongSuccess: (UiAudio) -> Unit,
        onPlaySongFailed: (UiAudio) -> Unit,
        job: Job?
    ) {
        audioPlaySuccessListener.add(onPlaySongSuccess)
        audioPlayFailedListener.add(onPlaySongFailed)
        job?.invokeOnCompletion {
            audioPlayFailedListener.remove(onPlaySongFailed)
            audioPlaySuccessListener.remove(onPlaySongSuccess)
        }
    }

    override suspend fun setOnPlaySongListener(
        onPlaySongSuccess: (UiAudio) -> Unit,
        onPlaySongFailed: (UiAudio) -> Unit,
        predicate: () -> Boolean
    ) {
        audioPlaySuccessListener.add(onPlaySongSuccess)
        audioPlayFailedListener.add(onPlaySongFailed)
        while (predicate()){
            //Wait
        }
        audioPlayFailedListener.remove(onPlaySongFailed)
        audioPlaySuccessListener.remove(onPlaySongSuccess)
    }

    override suspend fun setOnStopListener(
        job: Job?,
        onSongStopped: (UiAudio) -> Unit
    ) {
        audioStopListener.add(onSongStopped)
        job?.invokeOnCompletion {
            audioStopListener.remove(onSongStopped)
        }
    }

    override suspend fun setOnStopListener(
        predicate: () -> Boolean,
        onSongStopped: (UiAudio) -> Unit
    ) {
        audioStopListener.add(onSongStopped)
        while (predicate()){
            //Wait
        }
        audioStopListener.remove(onSongStopped)
    }

    override fun songProgression(coroutineScope: CoroutineScope): SharedFlow<Int> {
        return audioDataRepository.observeSongProgression()
            .shareIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(),
                replay = 1
            )
    }

    override fun lastSongProgress(context: Context): SharedFlow<Int> {
        collectLastProgressionJob.launchJob(context)
        return audioDataRepository.observeLastSongProgression(context)
            .shareIn(scope = scope, started = SharingStarted.WhileSubscribed())
    }

    override fun currentPlayingSong(): UiAudio? {
        return currentSong
    }

}