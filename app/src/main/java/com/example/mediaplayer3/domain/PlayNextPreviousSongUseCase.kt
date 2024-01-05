package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mplog.MPLogger
import javax.inject.Inject

class PlayNextPreviousSongUseCase @Inject constructor(
    private val playAudioUseCase: IPlayAudioUseCase,
    private val fetchDataUseCase: IFetchDataUseCase
) : IPlayNextOrPreviousSongUseCase {

    companion object {
        private const val CLASS_NAME = "PlayNextPreviousSongUseCase"
        private const val TAG = "AUDIO"
    }

    override fun playNext(currentSong: UiAudio, isRandom: Boolean, context: Context) {
        playAudioUseCase.let { iPlayAudioUseCase ->
            val audioList = fetchDataUseCase.getExtractedSongList()
            if (audioList.isEmpty()) {
                MPLogger.w(
                    CLASS_NAME,
                    "playNext",
                    TAG,
                    "audio list is empty so couldn't play next song"
                )
                return@let
            }
            val currentIndex = iPlayAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                audioList.indexOf(uiAudio)
            } ?: run { audioList.indexOf(currentSong) }
            val nextIndex = if (!isRandom) {
                if (currentIndex + 1 <= audioList.size - 1) currentIndex + 1 else 0
            } else {
                audioList.indices.random()
            }
            val nextItem = audioList[nextIndex]
            playAudioUseCase.playSong(context = context, uiAudio = nextItem, seekTo = 0)
        }
    }

    override fun playPrevious(currentSong: UiAudio, isRandom: Boolean, context: Context) {
        playAudioUseCase.let { iPlayAudioUseCase ->
            val audioList = fetchDataUseCase.getExtractedSongList()
            if (audioList.isEmpty()) {
                MPLogger.w(
                    CLASS_NAME,
                    "playPrevious",
                    TAG,
                    "audio list is empty so couldn't play previous song"
                )
                return@let
            }
            val currentIndex = iPlayAudioUseCase.currentPlayingSong()?.let { uiAudio ->
                audioList.indexOf(uiAudio)
            } ?: run { audioList.indexOf(currentSong) }
            val previousIndex = if (!isRandom) {
                if (currentIndex - 1 >= 0) currentIndex - 1 else audioList.size - 1
            } else {
                audioList.indices.random()
            }
            val previousItem = audioList[previousIndex]
            playAudioUseCase.playSong(context = context, uiAudio = previousItem, seekTo = 0)
        }
    }
}