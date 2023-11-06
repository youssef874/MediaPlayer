package com.example.mpmediamanager.internal

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AudioPlayerManagerImpl: IAudioPlayerManager {

    private var currentMediaPlayer: MediaPlayer? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var onProgress: ((Int)->Unit)? = null
    private var progressJob: Job? = null

    private fun startListenForProgress(){
        progressJob = coroutineScope.launch {
                while (currentMediaPlayer != null && currentMediaPlayer?.isPlaying == true){
                    currentMediaPlayer?.currentPosition?.let {
                        onProgress?.invoke(it)
                        delay(1000)
                    }
                }
        }
    }

    private fun stopListenToAudioChanges(){
        onProgress?.invoke(-1)
        progressJob?.cancel()
    }

    override fun playSong(context: Context, uri: Uri, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"playSong", TAG,"uri: $uri")
        currentMediaPlayer?.let {
            if (!it.isPlaying){
                MPLogger.d(CLASS_NAME,"playSong", TAG,"play song")
                if (seekTo != -1){
                    MPLogger.d(CLASS_NAME,"playSong", TAG,"seekTo: $seekTo")
                    it.seekTo(seekTo)
                }
                it.start()
            }else{
                MPLogger.e(CLASS_NAME,"playSong", TAG,"Song already playing cannot replay it")
            }
        }?:run {
            val mediaPlayer = getPlayerToPlay(context, uri)
            currentMediaPlayer = mediaPlayer
            if (!mediaPlayer.isPlaying){
                MPLogger.d(CLASS_NAME,"playSong", TAG,"play song")
                if (seekTo != -1){
                    MPLogger.d(CLASS_NAME,"playSong", TAG,"seekTo: $seekTo")
                    mediaPlayer.seekTo(seekTo)
                }
                mediaPlayer.start()
            }else{
                MPLogger.e(CLASS_NAME,"playSong", TAG,"ong already playing cannot replay it")
            }
        }
        startListenForProgress()
    }

    private fun getPlayerToPlay(context: Context,uri: Uri): MediaPlayer{
        val mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(context, uri)
            prepare()
        }
        return mediaPlayer
    }

    override fun stopSong(context: Context, uri: Uri) {
        MPLogger.d(CLASS_NAME,"stopSong", TAG,"uri: $uri")
            currentMediaPlayer?.let {
                MPLogger.d(CLASS_NAME,"stopSong", TAG,"top the song")
                it.stop()
                it.release()
            }?:run {
                MPLogger.e(CLASS_NAME,"stopSong", TAG,"cannot stop this song $uri as it is not playing")
            }
        cleanup()
    }

    override fun pauseSong(context: Context) {
        MPLogger.d(CLASS_NAME,"pauseSong", TAG,"pause")
            currentMediaPlayer?.let {
                MPLogger.d(CLASS_NAME,"pauseSong", TAG,"audioSessionId: ${it.audioSessionId}")
                it.pause()
            }?:{
                MPLogger.w(CLASS_NAME,"pauseSong", TAG,"there playing song to pause")
            }
        stopListenToAudioChanges()
    }

    override fun resumeSong(context: Context, seekTo: Int) {
        MPLogger.d(CLASS_NAME,"resumeSong", TAG,"resume")
            currentMediaPlayer?.let {
                MPLogger.d(CLASS_NAME,"resumeSong", TAG,"audioSessionId: ${it.audioSessionId}")
                if (seekTo != -1){
                    MPLogger.d(CLASS_NAME,"resumeSong", TAG,"seekTo: $seekTo")
                    it.seekTo(seekTo)
                }
                it.start()
            }?:run {
                MPLogger.w(CLASS_NAME,"resumeSong", TAG,"cannot resume the songs")
            }
        startListenForProgress()
    }

    override fun setOnDurationProgressListener(onDurationProgressListener: (duration: Int) -> Unit) {
        onProgress = onDurationProgressListener
    }

    override fun setSongCompleteListener(onComplete: () -> Unit) {
        currentMediaPlayer?.setOnCompletionListener {
            MPLogger.d(CLASS_NAME,"setSongCompleteListener", TAG,"audioSessionId: ${it.audioSessionId}")
            currentMediaPlayer?.stop()
            currentMediaPlayer?.release()
            currentMediaPlayer = null
            stopListenToAudioChanges()
            onComplete()
        }
    }

    override fun forward(forwardWith: Int) {
        currentMediaPlayer?.let {
            MPLogger.d(CLASS_NAME,"forward", TAG,"audioSessionId: ${it.audioSessionId}, forwardWith: $forwardWith")
            var newPosition = it.currentPosition + forwardWith
            if (newPosition > it.duration){
                newPosition = it.duration
            }
            it.seekTo(newPosition)
            it.start()
            startListenForProgress()
        }
    }

    override fun rewind(rewindWith: Int) {
        currentMediaPlayer?.let {
            MPLogger.d(CLASS_NAME,"rewind", TAG,"audioSessionId: ${it.audioSessionId}, rewindWith: $rewindWith")
            var newPosition = it.currentPosition - rewindWith
            if (newPosition < 0){
                newPosition = 0
            }
            it.seekTo(newPosition)
            it.start()
            startListenForProgress()
        }
    }

    override fun updatePlayerPosition(context: Context, uri: Uri, position: Int) {
        currentMediaPlayer?.seekTo(position) ?:run {
            MPLogger.d(CLASS_NAME,"updatePlayerPosition", TAG,"uri: $uri, position: $position")
            currentMediaPlayer = getPlayerToPlay(context, uri)
            currentMediaPlayer?.seekTo(position)
        }
    }

    private fun cleanup(){
            currentMediaPlayer = null
        stopListenToAudioChanges()
    }

    companion object{
        private const val CLASS_NAME = "AudioPlayerManagerImpl"
        private const val TAG = "AUDIO_PLAYER"
    }
}