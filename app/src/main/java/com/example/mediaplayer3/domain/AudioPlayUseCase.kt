package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioRepository
import com.example.mpdataprovider.datadtore.RepeatMode
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

object AudioPlayUseCase : IAudioPlayUseCase,IUseCase {

    private lateinit var audioRepository: IAudioRepository
    private var coroutineScope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope get() = coroutineScope
    private var isRandom = false
    @RepeatMode
    private var repeatMode = RepeatMode.NO_REPEAT
    private val randomCollectionJobScheduler: IUseCaseJobScheduler by UseCaseScheduler{list->
        var context: Context? = null
        list.forEach {arg->
            if (arg is Context){
                context = arg
            }
        }
        context?.let {
            AudioConfigurationUseCase.isRandomModeInFlow(it).collectLatest {value ->
                MPLogger.d(CLASS_NAME,"randomCollectionJobScheduler", TAG,"value: $value")
                isRandom = value
            }
        }
    }

    private val RepeatModeJobScheduler: IUseCaseJobScheduler by  UseCaseScheduler{ list->
        var context: Context? = null
        list.forEach {arg->
            if (arg is Context){
                context = arg
            }
        }
        context?.let {
            AudioConfigurationUseCase.getRepeatMode(it).collectLatest {value->
                MPLogger.d(CLASS_NAME,"needToRepeatJobScheduler", TAG,"value: $value")
                repeatMode = value
            }
        }
    }

    private val songPlaySuccessListeners = mutableListOf<(UiAudio) -> Unit>()
    private val songPlayFailedListeners = mutableListOf<(UiAudio) -> Unit>()
    private val songStopPlayListeners = mutableListOf<(UiAudio) -> Unit>()
    private var currentSong: UiAudio? = null

    operator fun invoke(audioRepository: IAudioRepository, coroutineScope: CoroutineScope) {
        AudioPlayUseCase.audioRepository = audioRepository
        AudioPlayUseCase.coroutineScope = coroutineScope
    }

    private var _songProgressionJob: Job? = null
    override val songProgressionJob: Job?
        get() = _songProgressionJob


    override fun playSong(context: Context, uiAudio: UiAudio, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"playSong", TAG,"songId: ${uiAudio.id}, seekTo: $seekTo")
        randomCollectionJobScheduler.launchJob(context)
        RepeatModeJobScheduler.launchJob(context)
        currentSong?.let {
            MPLogger.d(CLASS_NAME,"playSong", TAG,"stopSong songId: ${it.id}")
            audioRepository.stopSong(context, it.uri)
            songStopPlayListeners.forEach { func ->
                func(it)
            }
        }
        try {
            audioRepository.playSong(context, uiAudio.uri, seekTo)
            currentSong = uiAudio
            songPlaySuccessListeners.forEach { func ->
                func(uiAudio)
            }
            _songProgressionJob = coroutineScope.launch {
                getPlayingSongDuration(context).collect()
            }
            audioRepository.onSongCompletionListener {
                when(repeatMode){
                    RepeatMode.NO_REPEAT->{
                        with(SongExtractorUseCase.getExtractedSongList()) {
                            firstOrNull {
                                it.id == uiAudio.id
                            }?.let {
                                val nextIndex: Int = if (!isRandom){
                                    val index = if (indexOf(it) + 1 <= size) indexOf(it) + 1 else size - 1
                                    index
                                }else{
                                    indices.random()
                                }
                                val nextSong = get(nextIndex)
                                MPLogger.d(CLASS_NAME,"playSong", TAG,"song completed songId: ${nextSong.id}")
                                playSong(context, nextSong)
                                currentSong = nextSong
                                songPlaySuccessListeners.forEach { function ->
                                    function(nextSong)
                                }
                            }
                        }
                    }
                    RepeatMode.ONE_REPEAT->{
                        MPLogger.d(CLASS_NAME,"playSong", TAG,"song completed songId: ${uiAudio.id}")
                        playSong(context, uiAudio)
                        currentSong = uiAudio
                        songPlaySuccessListeners.forEach {function ->
                            function(uiAudio)
                        }
                    }
                    RepeatMode.REPEAT_ALL->{
                        with(SongExtractorUseCase.getExtractedSongList()) {
                            firstOrNull {
                                it.id == uiAudio.id
                            }?.let {
                                val nextIndex: Int = if (!isRandom){
                                    val index = if (indexOf(it) + 1 <= size) indexOf(it) + 1 else 0
                                    index
                                }else{
                                    indices.random()
                                }
                                val nextSong = get(nextIndex)
                                MPLogger.d(CLASS_NAME,"playSong", TAG,"song completed songId: ${nextSong.id}")
                                playSong(context, nextSong)
                                currentSong = nextSong
                                songPlaySuccessListeners.forEach { function ->
                                    function(nextSong)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            MPLogger.e(CLASS_NAME,"playSong", TAG,"song filed to play songId: ${uiAudio.id}, message: ${e.message}")
            songPlayFailedListeners.forEach {
                it(uiAudio)
            }
        }

    }

    override fun stopSong(context: Context) {
        songProgressionJob?.cancel()
        randomCollectionJobScheduler.cancelJob()
        _songProgressionJob = null
        currentSong?.let {
            MPLogger.d(CLASS_NAME,"stopSong", TAG,"songId: ${it.id}")
            audioRepository.stopSong(context, it.uri)
            songStopPlayListeners.forEach { function ->
                function(it)
            }
        } ?: run {
            MPLogger.w(CLASS_NAME,"stopSong", TAG,"No playing song to stop")
        }
    }

    override fun getCurrentPlayingSong(): UiAudio? {
        return currentSong
    }

    override fun getLastPlayingSongDuration(context: Context): SharedFlow<Int> {
        return audioRepository.getLastPlayingSongDuration(context = context)
            .shareIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed())
    }

    override fun setOnSongPlayResultListener(
        onSongPlaySuccess: (UiAudio) -> Unit,
        onSongPlayFailed: (UiAudio) -> Unit
    ) {
        songPlaySuccessListeners.add(onSongPlaySuccess)
        songPlayFailedListeners.add(onSongPlayFailed)
    }

    override fun setOnSongStopPlayListener(onSongStopPlay: (UiAudio) -> Unit) {
        songStopPlayListeners.add(onSongStopPlay)
    }

    override fun getPlayingSongDuration(context: Context): SharedFlow<Int> {
        return audioRepository.songPlayingProgress(context)
            .shareIn(coroutineScope, started = SharingStarted.WhileSubscribed())
    }

    override fun getLastPlayingSong(context: Context): SharedFlow<Long> {
        return audioRepository.getLastPlayingSong(context)
            .shareIn(coroutineScope, started = SharingStarted.WhileSubscribed())
    }

    private const val CLASS_NAME = "AudioPlayUseCase"
    private const val TAG = "AUDIO"
}