package com.example.mediaplayer3.domain

import android.content.Context
import android.net.Uri
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AudioForwardOrRewindUseCaseImp : IAudioForwardOrRewindUseCase, IUseCase {

    private const val CLASS_NAME = "AudioForwardOrRewindUseCaseImp"
    private const val TAG = "AUDIO"

    private var audioDataRepo: IAudioDataRepo? = null
    private var playUseCase: IPlayAudioUseCase? = null


    private var _scope = CoroutineScope(Dispatchers.Default)
    override val scope: CoroutineScope
        get() = _scope

    operator fun invoke(audioDataRepo: IAudioDataRepo, scope: CoroutineScope,playUseCase: IPlayAudioUseCase) {
        this.audioDataRepo = audioDataRepo
        this._scope = scope
        this.playUseCase = playUseCase
    }

    override fun forward(forwardAt: Int) {
        playUseCase?.currentPlayingSong()?.let {
            MPLogger.d(CLASS_NAME, "forward", TAG, "forwardAt: $forwardAt")
            audioDataRepo?.forward(forwardAt)
        }?:run {
            MPLogger.w(CLASS_NAME,"forward", TAG,"no current song to forward")
        }
    }

    override fun rewind(rewindAt: Int) {
        playUseCase?.currentPlayingSong()?.let {
            MPLogger.d(CLASS_NAME, "rewind", TAG, "rewindAt: $rewindAt")
            audioDataRepo?.rewind(rewindAt)
        }?:run {
            MPLogger.w(CLASS_NAME,"rewind", TAG,"no current song to rewind")
        }
    }

    override fun setPlayingPosition(context: Context, uri: Uri, position: Int) {
        MPLogger.d(CLASS_NAME,"setPlayingPosition", TAG,"uri: $uri, position: $position")
        scope.launch {
            audioDataRepo?.setPlayingPosition(context, uri, position)
        }
    }
}