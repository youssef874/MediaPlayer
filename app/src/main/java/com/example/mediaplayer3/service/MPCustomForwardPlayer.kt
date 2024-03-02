package com.example.mediaplayer3.service

import android.content.Context
import android.os.Looper
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.util.UnstableApi
import com.example.mediaplayer3.domain.IAudioConfiguratorUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.IPlayNextOrPreviousSongUseCase
import kotlinx.coroutines.CoroutineScope

@UnstableApi
class MPCustomForwardPlayer(
    private val playUseCase: IPlayAudioUseCase,
    fetchDataUseCase: IFetchDataUseCase,
    configuratorUseCase: IAudioConfiguratorUseCase,
    resumePauseSongUseCaseImpl: IAudioPauseOrResumeUseCase,
    playNextOrPreviousSongUseCase: IPlayNextOrPreviousSongUseCase,
    coroutineScope: CoroutineScope,
    private val context: Context,
    looper: Looper
) : ForwardingPlayer(
    MPCustomMediaThreePlayer(
        playUseCase,
        fetchDataUseCase,
        configuratorUseCase,
        resumePauseSongUseCaseImpl,
        playNextOrPreviousSongUseCase,
        coroutineScope,
        context,
        looper
    )
) {

    override fun getCurrentPosition(): Long {
        return playUseCase.currentSongProgression.toLong()
    }

    override fun getDuration(): Long {
        return playUseCase.currentPlayingSong()?.duration?.toLong() ?: 0L
    }

    override fun release() {
        playUseCase.stopSong(context)
        super.release()
    }

    override fun stop() {
        playUseCase.stopSong(context)
        super.stop()
    }
}