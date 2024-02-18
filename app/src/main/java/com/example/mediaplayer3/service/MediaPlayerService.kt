package com.example.mediaplayer3.service

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Looper
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.Player.Commands
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.mediaplayer3.domain.IAudioConfiguratorUseCase
import com.example.mediaplayer3.domain.IAudioPauseOrResumeUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.domain.IPlayAudioUseCase
import com.example.mediaplayer3.domain.IPlayNextOrPreviousSongUseCase
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.notification.MediaPlayerMPNotificationImpl
import com.example.mediaplayer3.notification.NotificationManager
import com.example.mediaplayer3.service.delegate.ServiceJobScheduler
import com.example.mediaplayer3.viewModel.delegates.IJobController
import com.example.mplog.MPLogger
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MediaPlayerService : MediaSessionService(), IBaseService {

    companion object {
        private const val CLASS_NAME = "MediaPlayerService"
        private const val TAG = "BACKGROUND"
        const val START = "start"
        const val STOP = "stop"
        private const val PLAY_NEXT_COMMAND = "play_next_action"
        private const val PLAY_PREVIOUS_COMMAND = "play_previous_action"
    }


    private val serviceScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var playAudioUseCase: IPlayAudioUseCase

    @Inject
    lateinit var fetchDataUseCase: IFetchDataUseCase

    @Inject
    lateinit var playNextOrPreviousSongUseCase: IPlayNextOrPreviousSongUseCase

    @Inject
    lateinit var pauseOrResumeUseCase: IAudioPauseOrResumeUseCase

    @Inject
    lateinit var audioConfiguratorUseCase: IAudioConfiguratorUseCase

    private val player: Player? = Looper.myLooper()?.let {
        object : SimpleBasePlayer(it) {
            override fun getState(): State {
                val index = fetchDataUseCase.getExtractedSongList()
                    .indexOf(playAudioUseCase.currentPlayingSong())
                return State.Builder()
                    .setAvailableCommands(
                        Commands.Builder()
                            .addAll(
                                Player.COMMAND_PLAY_PAUSE,
                                Player.COMMAND_SEEK_TO_PREVIOUS,
                                Player.COMMAND_SEEK_TO_NEXT,
                                Player.COMMAND_GET_CURRENT_MEDIA_ITEM,
                                Player.COMMAND_SET_PLAYLIST_METADATA
                            ).build()
                    ).setPlayWhenReady(true, Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
                    .setCurrentMediaItemIndex(index)
                    .setContentPositionMs(playAudioUseCase.currentSongProgression.toLong())
                    .setTotalBufferedDurationMs(
                        PositionSupplier.getConstant(
                            playAudioUseCase.currentPlayingSong()?.duration?.toLong() ?: 0L
                        )
                    )
                    .setPlaylist(
                        fetchDataUseCase.getExtractedSongList().map {
                            MediaItemData.Builder(it.id).build()
                        }
                    )
                    .setPlaybackState(if (playAudioUseCase.isPlaying) STATE_READY else STATE_IDLE)
                    .build()
            }

            override fun handleSetPlayWhenReady(playWhenReady: Boolean): ListenableFuture<*> {

                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    MPLogger.i(
                        CLASS_NAME,
                        "handleSetPlayWhenReady",
                        TAG,
                        "uiAudio: $uiAudio, isPlaying: $isPlaying , ${playAudioUseCase.isPlaying}, playWhenReady: $playWhenReady"
                    )
                    state
                }
                return Futures.immediateVoidFuture()
            }

            override fun handleSeek(
                mediaItemIndex: Int,
                positionMs: Long,
                seekCommand: Int
            ): ListenableFuture<*> {
                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    MPLogger.i(
                        CLASS_NAME,
                        "handleSeek",
                        TAG,
                        "uiAudio: $uiAudio, ${mediaSession?.controllerForCurrentRequest?.packageName}"
                    )
                }
                return super.handleSeek(mediaItemIndex, positionMs, seekCommand)
            }
        }
    }

    private val forwardingPlayer: ForwardingPlayer? = player?.let {
        object : ForwardingPlayer(it) {

            override fun play() {
                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    MPLogger.i(
                        CLASS_NAME,
                        "play",
                        TAG,
                        "uiAudio: $uiAudio, ${mediaSession?.controllerForCurrentRequest?.packageName}"
                    )
                    if (!playAudioUseCase.isPlaying){
                        playAudioUseCase.playSong(this@MediaPlayerService, uiAudio)
                    }
                }
                super.play()
            }

            override fun pause() {
                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    MPLogger.i(
                        CLASS_NAME,
                        "pause",
                        TAG,
                        "uiAudio: $uiAudio, ${mediaSession?.controllerForCurrentRequest?.packageName}"
                    )
                    pauseOrResumeUseCase.pauseSong(this@MediaPlayerService, uiAudio)
                }
                super.pause()
            }

            override fun stop() {
                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    MPLogger.i(
                        CLASS_NAME,
                        "stop",
                        TAG,
                        "uiAudio: $uiAudio, ${mediaSession?.controllerForCurrentRequest?.packageName}"
                    )
                    pauseOrResumeUseCase.pauseSong(this@MediaPlayerService, uiAudio)
                }
                super.stop()
            }

            override fun seekToNext() {
                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    collectIsRandomJobScheduler.cancelJob()
                    MPLogger.i(
                        CLASS_NAME,
                        "seekToNext",
                        TAG,
                        "uiAudio: $uiAudio, ${mediaSession?.controllerForCurrentRequest?.packageName}"
                    )
                    collectIsRandomJobScheduler.launchJob(
                        this@MediaPlayerService,
                        PLAY_NEXT_COMMAND
                    )
                }
                super.seekToNext()
            }

            override fun seekToPrevious() {
                playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                    collectIsRandomJobScheduler.cancelJob()
                    MPLogger.i(CLASS_NAME, "seekToPrevious", TAG, "uiAudio: $uiAudio")
                    collectIsRandomJobScheduler.launchJob(
                        this@MediaPlayerService,
                        PLAY_PREVIOUS_COMMAND
                    )
                }
                super.seekToPrevious()
            }

            override fun getCurrentPosition(): Long {
                return playAudioUseCase.currentSongProgression.toLong()
            }

            override fun getDuration(): Long {
                return playAudioUseCase.currentPlayingSong()?.duration?.toLong()?:0L
            }

        }
    }

    private var mediaSession: MediaSession? = null

    private var mediaPlayerNotification = mediaSession?.let {
            NotificationManager.getMediaNotification(
                uiAudio = playAudioUseCase.currentPlayingSong() ?: UiAudio(),
                mediaSession = it
            )
        }

    private val collectIsRandomJobScheduler: IJobController by ServiceJobScheduler { args ->
        var context: Context? = null
        var action = ""
        args.forEach {
            if (it is Context) {
                context = it
            }
            if (it is String) {
                action = it
            }
        }
        context?.let { cont ->
            audioConfiguratorUseCase.isRandomModeInFlow(cont).collectLatest {
                MPLogger.d(CLASS_NAME, "isRandomModeInFlow", TAG, "isRandom: $it")
                if (action == PLAY_NEXT_COMMAND) {
                    playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                        playNextOrPreviousSongUseCase.playNext(
                            currentSong = uiAudio,
                            isRandom = it,
                            context = cont
                        )
                    }
                    collectIsRandomJobScheduler.cancelJob()
                } else if (action == PLAY_PREVIOUS_COMMAND) {
                    playAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                        playNextOrPreviousSongUseCase.playPrevious(
                            currentSong = uiAudio,
                            context = cont,
                            isRandom = it
                        )
                    }
                    collectIsRandomJobScheduler.cancelJob()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = forwardingPlayer?.let {
            MediaSession.Builder(this, it)
                .build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    private fun refreshNotification(){
        mediaSession?.let {
            NotificationManager.getMediaNotification(
                uiAudio = playAudioUseCase.currentPlayingSong() ?: UiAudio(),
                mediaSession = it
            ).showNotification(this)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onDestroy() {
        handleStopServiceRequest()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            playAudioUseCase.setOnPlaySongListener(
                onPlaySongSuccess = { uiAudio ->
                    MPLogger.d(CLASS_NAME,"onStartCommand", TAG,"onPlaySongSuccess: uiAudio: $uiAudio")
                    mediaPlayerNotification?.showNotification(this@MediaPlayerService)
                    if (!playAudioUseCase.isPlaying){
                        forwardingPlayer?.play()
                    }
                    refreshNotification()
                },
                onPlaySongFailed = { _ ->
                    MPLogger.d(CLASS_NAME,"onStartCommand", TAG,"onPlaySongFailed")
                    mediaPlayerNotification?.showNotification(this@MediaPlayerService)
                    refreshNotification()
                },
                predicate = { isActive }
            )
        }
        serviceScope.launch {
            playAudioUseCase.setOnStopListener(
                predicate = { isActive },
                onSongStopped = { _ ->
                    MPLogger.d(CLASS_NAME,"onStartCommand", TAG,"onSongStopped")
                    mediaPlayerNotification?.showNotification(this@MediaPlayerService)
                    forwardingPlayer?.pause()
                    refreshNotification()
                }
            )
        }
        when (intent?.action) {
            START -> {
                MPLogger.d(
                    CLASS_NAME, "onStartCommand",
                    TAG, "start service isPlaying: ${playAudioUseCase.isPlaying}"
                )
                handleStartServiceRequest()
            }

            STOP -> {
                MPLogger.d(
                    CLASS_NAME, "onStartCommand",
                    TAG, "stop service"
                )
                handleStopServiceRequest()
            }
            MediaPlayerMPNotificationImpl.CANCEL_NOTIFICATION->{
                MPLogger.d(
                    CLASS_NAME, "onStartCommand",
                    TAG, "notification canceled"
                )
                handleStopServiceRequest()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun handleStartServiceRequest() {
        if (!playAudioUseCase.isPlaying) {
            MPLogger.e(
                CLASS_NAME,
                "handleStartServiceRequest",
                TAG,
                "no song is playing do not start this service"
            )
            return
        }
        refreshNotification()
        val notification = mediaPlayerNotification?.createNotification(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (notification != null) {
                startForeground(
                    NotificationManager.MEDIA_PLAYER_NOTIFICATION_ID.toInt(),
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            }
        } else {
            startForeground(NotificationManager.MEDIA_PLAYER_NOTIFICATION_ID.toInt(), notification)
        }
        forwardingPlayer?.play()

    }

    override fun handleStopServiceRequest() {
        MPLogger.e(
            CLASS_NAME,
            "handleStopServiceRequest",
            TAG,
            "stop service"
        )
        collectIsRandomJobScheduler.cancelJob()
        mediaPlayerNotification?.cancelNotification(this)
        mediaSession?.run {
            this.player.release()
            release()
            mediaSession = null
        }
        stopSelf()
    }

    override val scope: CoroutineScope
        get() = serviceScope
}