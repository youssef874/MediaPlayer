package com.example.mediaplayer3.service

import android.content.Context
import android.os.Looper
import androidx.media3.common.Player
import androidx.media3.common.Player.Commands
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.common.util.UnstableApi
import com.example.mediaplayer3.domain.IAudioConfiguratorUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.IPlayNextOrPreviousSongUseCase
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mplog.MPLogger
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * This class represent a player custom implementation for
 * media 3 [MediaSession]
 */
@UnstableApi
class MPCustomMediaThreePlayer(
    private val playUseCase: IPlayAudioUseCase,
    private val fetchDataUseCase: IFetchDataUseCase,
    private val configuratorUseCase: IAudioConfiguratorUseCase,
    private val resumePauseSongUseCaseImpl: IAudioPauseOrResumeUseCase,
    private val playNextOrPreviousSongUseCase: IPlayNextOrPreviousSongUseCase,
    private val coroutineScope: CoroutineScope,
    private val context: Context,
    looper: Looper
) : SimpleBasePlayer(looper) {

    companion object {
        private const val CLASS_NAME = "MPCustomMediaThreePlayer"
        private const val TAG = "SERVICE"
    }

    private var collectIsRandomJob: Job? = null

    private fun launchCollectIsRandomJob(commands: Int,uiAudio: UiAudio){
        collectIsRandomJob = coroutineScope.launch {
            configuratorUseCase.isRandomModeInFlow(context).collectLatest {
                if (commands == COMMAND_SEEK_TO_NEXT){
                    playNextOrPreviousSongUseCase.playNext(uiAudio,it,context)
                }else if(commands == COMMAND_SEEK_TO_PREVIOUS){
                    playNextOrPreviousSongUseCase.playPrevious(uiAudio,it,context)
                }
                cancelCollectIsRandomJob()
            }
        }
    }

    private fun cancelCollectIsRandomJob(){
        collectIsRandomJob?.cancel()
        collectIsRandomJob = null
    }

    override fun getState(): State {
        MPLogger.i(CLASS_NAME, "getState", TAG, "build player State")
        val currentSongIndex = fetchDataUseCase.getExtractedSongList()
            .indexOf(playUseCase.currentPlayingSong())
        return State.Builder()
            .setAvailableCommands(
                Commands.Builder()
                    .addAll(
                        COMMAND_PLAY_PAUSE,
                        COMMAND_SEEK_TO_PREVIOUS,
                        COMMAND_SEEK_TO_NEXT,
                        COMMAND_GET_CURRENT_MEDIA_ITEM,
                        COMMAND_SET_PLAYLIST_METADATA

                    ).build()
            ).setPlayWhenReady(true, Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
            .setCurrentMediaItemIndex(currentSongIndex)
            .setPlaylist(
                fetchDataUseCase.getExtractedSongList().map {
                    MediaItemData.Builder(it.id).build()
                }
            )
            .setPlaybackState(if (playUseCase.isPlaying) STATE_READY else STATE_IDLE)
            .build()
    }

    override fun handleSetPlayWhenReady(playWhenReady: Boolean): ListenableFuture<*> {
            playUseCase.currentPlayingSong()?.let { uiAudio ->
                MPLogger.d(
                    CLASS_NAME,
                    "handleSetPlayWhenReady",
                    TAG,
                    "uiAudio: $uiAudio, is custom player playing: $isPlaying , is app playing: ${playUseCase.isPlaying}, playWhenReady: playWhenReady"
                )

                if (playUseCase.isPlaying) {
                    resumePauseSongUseCaseImpl.pauseSong(context, uiAudio)
                } else {
                    playUseCase.playSong(context, uiAudio)
                }
            }
        return Futures.immediateVoidFuture()
    }

    override fun handleSeek(
        mediaItemIndex: Int,
        positionMs: Long,
        seekCommand: Int
    ): ListenableFuture<*> {
        playUseCase.currentPlayingSong()?.let { uiAudio ->
            MPLogger.d(
                CLASS_NAME,
                "handleSeek",
                TAG,
                "uiAudio: $uiAudio, seekCommand: $seekCommand"
            )
            launchCollectIsRandomJob(seekCommand,uiAudio)
        }
        return Futures.immediateVoidFuture()
    }
}