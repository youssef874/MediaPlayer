package com.example.mediaplayer3.service

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Looper
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
                if (action == MediaPlayerMPNotificationImpl.SHUFFLE_ACTION) {
                    mediaSession?.player?.shuffleModeEnabled = it
                }
                collectIsRandomJobScheduler.cancelJob()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = Looper.myLooper()?.let {
            MPCustomForwardPlayer(
                playUseCase = playAudioUseCase,
                fetchDataUseCase = fetchDataUseCase,
                configuratorUseCase = audioConfiguratorUseCase,
                resumePauseSongUseCaseImpl = pauseOrResumeUseCase,
                playNextOrPreviousSongUseCase = playNextOrPreviousSongUseCase,
                context = this,
                coroutineScope = scope,
                looper = it
            ).let { mpCustomMediaThreePlayer ->
                MediaSession.Builder(this, mpCustomMediaThreePlayer)
                    .build()
            }
        }

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        super.onUpdateNotification(session, startInForegroundRequired)
        MPLogger.d(
            CLASS_NAME,
            "onUpdateNotification",
            TAG,
            "startInForegroundRequired: $startInForegroundRequired, session: $session"
        )
    }

    private fun refreshNotification() {
        mediaSession?.let {
            mediaPlayerNotification = NotificationManager.getMediaNotification(
                uiAudio = playAudioUseCase.currentPlayingSong() ?: UiAudio(),
                mediaSession = it
            )
            mediaPlayerNotification?.showNotification(this)
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
                    MPLogger.d(
                        CLASS_NAME,
                        "onStartCommand",
                        TAG,
                        "onPlaySongSuccess: uiAudio: $uiAudio"
                    )
                    mediaSession?.player?.play()
                    refreshNotification()
                },
                onPlaySongFailed = { _ ->
                    MPLogger.d(CLASS_NAME, "onStartCommand", TAG, "onPlaySongFailed")
                    refreshNotification()
                },
                predicate = { isActive }
            )
        }
        serviceScope.launch {
            playAudioUseCase.setOnStopListener(
                predicate = { isActive },
                onSongStopped = { _ ->
                    MPLogger.d(CLASS_NAME, "onStartCommand", TAG, "onSongStopped")
                    mediaPlayerNotification?.showNotification(this@MediaPlayerService)
                    mediaSession?.player?.pause()
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

            MediaPlayerMPNotificationImpl.CANCEL_NOTIFICATION -> {
                MPLogger.d(
                    CLASS_NAME, "onStartCommand",
                    TAG, "notification canceled"
                )
                handleStopServiceRequest()
            }

            MediaPlayerMPNotificationImpl.SHUFFLE_ACTION -> {
                MPLogger.d(
                    CLASS_NAME, "onStartCommand",
                    TAG, "change is repeat mode"
                )
                collectIsRandomJobScheduler.launchJob(
                    this,
                    MediaPlayerMPNotificationImpl.SHUFFLE_ACTION
                )
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

    }

    override fun handleStopServiceRequest() {
        MPLogger.d(
            CLASS_NAME,
            "handleStopServiceRequest",
            TAG,
            "stop service"
        )
        collectIsRandomJobScheduler.cancelJob()
        scope.cancel()
        mediaSession?.run {
            player.stop()
            release()
        }
        mediaSession = null
        mediaPlayerNotification?.cancelNotification(this)
        stopSelf()
    }

    override val scope: CoroutineScope
        get() = serviceScope
}