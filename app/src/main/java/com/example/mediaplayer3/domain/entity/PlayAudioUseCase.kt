package com.example.mediaplayer3.domain.entity

import android.content.Context
import com.example.mediaplayer3.domain.FetchDataUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.IUseCase
import com.example.mediaplayer3.domain.IUseCaseJobScheduler
import com.example.mediaplayer3.domain.UseCaseScheduler
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PlayAudioUseCase: IPlayAudioUseCase, IUseCase {

    private const val CLASS_NAME = "PlayAudioUseCase"
    private const val TAG = "AUDIO"

    private var sScope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = sScope

    private var audioDataRepository: IAudioDataRepo? = null
    private var fetchDataUseCase: IFetchDataUseCase = FetchDataUseCase

    private val audioPlaySuccessListener = mutableListOf< (UiAudio)->Unit>()
    private val audioPlayFailedListener = mutableListOf< (UiAudio)->Unit>()
    private val audioStopListener = mutableListOf< (UiAudio)->Unit>()

    private var currentSong: UiAudio? = null

    private var _isPlaying = false
    override val isPlaying: Boolean
        get() = _isPlaying

    private val progressSongJobScheduler: IUseCaseJobScheduler by UseCaseScheduler{list->
        var context: Context? = null
        list.forEach {arg->
            if (arg is Context){
                context = arg
            }
        }
        songProgression(scope)?.collectLatest { progress->
            MPLogger.d(CLASS_NAME,"progressSongJobScheduler", TAG,"progress: $progress")
            context?.let {
                audioDataRepository?.updateLastSongProgress(it,progress)
            }
        }
    }

    operator fun invoke(audioDataRepo: IAudioDataRepo, fetchDataUseCase: IFetchDataUseCase){
        this.audioDataRepository = audioDataRepo
        this.fetchDataUseCase = fetchDataUseCase
    }


    override fun playSong(
        context: Context,
        uiAudio: UiAudio,
        seekTo: Int
    ) {
        MPLogger.d(CLASS_NAME,"playSong", TAG,"try to play: $uiAudio")
        try {
            if (seekTo != -1){
                MPLogger.d(CLASS_NAME,"playSong", TAG,"play: $uiAudio, at: $seekTo")
                scope.launch {
                    audioDataRepository?.updateLastSongProgress(context,seekTo)
                }
                currentSong?.let {
                    audioDataRepository?.stopSong(context,it.uri)
                }
                audioDataRepository?.playSong(context,uiAudio.uri,seekTo)
                _isPlaying = true
            }else{
                var job: Job? = null
                currentSong?.let {
                    audioDataRepository?.stopSong(context,it.uri)
                }
                job = scope.launch {
                    lastSongProgress(context)?.collectLatest {
                        MPLogger.d(CLASS_NAME,"playSong", TAG,"lastSongProgress: $it")
                        if (it != -1){
                            audioDataRepository?.playSong(context, uiAudio.uri,it)
                        }else{
                            audioDataRepository?.playSong(context, uiAudio.uri)
                        }
                        job?.cancel()
                    }
                }
            }
            progressSongJobScheduler.launchJob(context)
            scope.launch {
                withContext(Dispatchers.IO){
                    audioDataRepository?.updateLastPlayingSong(context,uiAudio.id)
                }
            }
            audioDataRepository?.observeSongCompletion {
                MPLogger.d(CLASS_NAME,"onSongCompleted", TAG,"current playing song completed")
                val audioList = fetchDataUseCase.getExtractedSongList()
                val currentSongIndex = audioList.indexOf(uiAudio)
                val nextSongIndex = if (currentSongIndex +1<= audioList.size-1) currentSongIndex+1 else audioList.size-1
                val nextSong = audioList[nextSongIndex]
                MPLogger.d(CLASS_NAME,"onSongCompleted", TAG,"nextSong: $nextSong")
                playSong(context,nextSong)
            }
            _isPlaying = true
            currentSong = uiAudio
            audioPlaySuccessListener.forEach {
                it(uiAudio)
            }
        }catch (_: Exception){
            MPLogger.w(CLASS_NAME,"playSong", TAG,"Failed to play $uiAudio")
            _isPlaying = false
            audioPlayFailedListener.forEach {
                it(uiAudio)
            }
        }
    }

    override fun stopSong(context: Context) {
        MPLogger.i(CLASS_NAME,"stopSong", TAG,"try to stop song")
        currentSong?.let {uiAudio->
            MPLogger.i(CLASS_NAME,"stopSong", TAG,"stop playing: $uiAudio")
            audioDataRepository?.stopSong(context,uiAudio.uri)
            progressSongJobScheduler.cancelJob()
            audioStopListener.forEach {
                it(uiAudio)
            }
            _isPlaying = false
            currentSong = null
        }?:run{
            MPLogger.w(CLASS_NAME,"stopSong", TAG,"There no song was playing to stop")
        }
    }

    override suspend fun setOnPlaySongListener(
        onPlaySongSuccess: (UiAudio) -> Unit,
        onPlaySongFailed: (UiAudio) -> Unit,
        job: Job?
    ) {
       audioPlaySuccessListener.add(onPlaySongSuccess)
        audioPlayFailedListener.add(onPlaySongFailed)
        job?.invokeOnCompletion {
            audioPlayFailedListener.remove (onPlaySongFailed)
            audioPlaySuccessListener.remove(onPlaySongSuccess)
        }
    }

    override suspend fun setOnStopListener(
        job: Job?,
        onSongStopped: (UiAudio) -> Unit
    ) {
        audioStopListener.add(onSongStopped)
        job?.invokeOnCompletion {
            audioStopListener.remove (onSongStopped)
        }
    }

    override fun songProgression(coroutineScope: CoroutineScope): SharedFlow<Int>? {
        return audioDataRepository?.observeSongProgression()
            ?.shareIn(scope = coroutineScope, started = SharingStarted.WhileSubscribed(), replay = 1)
    }

    override fun lastSongProgress(context: Context): SharedFlow<Int>? {
        return audioDataRepository?.observeLastSongProgression(context)?.shareIn(scope = scope, started = SharingStarted.WhileSubscribed())
    }

    override fun currentPlayingSong(): UiAudio? {
        return currentSong
    }

}