package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioRepository
import com.example.mpdataprovider.contentprovider.data.MPAudio
import com.example.mplog.MPLogger

object SongExtractorUseCase: ISongExtractorUseCase {

    private lateinit var audioRepository: IAudioRepository

    private var audioList = mutableListOf<UiAudio>()
    private const val CLASS_NAME = "SongExtractorUseCase"
    private const val TAG = "AUDIO"

    init {
        SongEditUseCase.onFavoriteChangesForAudio {uiAudio->
            MPLogger.d(CLASS_NAME,"onFavoriteChange", TAG,"uiAudio: $uiAudio")
            if (audioList.isNotEmpty()){
                audioList.find { it.id == uiAudio.id }?.let {
                    val index = audioList.indexOf(it)
                    audioList[index] = uiAudio
                }
            }
        }
    }
    
    operator fun invoke(audioRepository: IAudioRepository){
        this.audioRepository = audioRepository
    }

    override suspend fun getSongs(
        context: Context,
        filterByName: (() -> String)?,
        filterByAlbum: (() -> String)?,
        filterByArtist: (() -> String)?
    ): Result<List<UiAudio>> {
       return if (filterByName != null){
           audioRepository.getSongsBySongName(context,filterByName()).toListUiAudio().run {
               if (this is Result.Success){
                   audioList.addAll(data)
               }
               this
           }
       }else if (filterByAlbum != null){
           audioRepository.getSongsByAlbum(context,filterByAlbum()).toListUiAudio().run {
               if (this is Result.Success){
                   audioList.addAll(data)
               }
               this
           }
       }else if (filterByArtist != null){
           audioRepository.getSongsByArtist(context,filterByArtist()).toListUiAudio().run {
               if (this is Result.Success){
                   audioList.addAll(data)
               }
               this
           }
       }else{
           audioRepository.getAllSongs(context).toListUiAudio().run {
               if (this is Result.Success){
                   audioList.addAll(data)
               }
               this
           }
       }
    }

    override suspend fun getSong(context: Context, id: Long): Result<UiAudio?> {
        return audioRepository.getSongById(context,id).toResultUiAudio()
    }

    override fun getExtractedSongList(): List<UiAudio> {
        return audioList
    }
}

fun MPAudio.toUiAudio(): UiAudio{
    return UiAudio(
        id = id, uri = uri, duration = duration, size = size, artistName = artistName,
        album = album, songName = songName, albumThumbnailUri = albumThumbnailUri
    )
}

fun Result<MPAudio?>.toResultUiAudio(): Result<UiAudio?>{
    return when(this){
        is Result.Success->{
            Result.Success(data?.toUiAudio())
        }
        is Result.Error->{
            Result.Error(t)
        }
    }
}

fun Result<List<MPAudio>>.toListUiAudio(): Result<List<UiAudio>> {
    return when (this) {
        is Result.Success -> {
            val list = this.data.toMutableList().map {
                UiAudio(
                    id = it.id,
                    songName = it.songName,
                    uri = it.uri,
                    size = it.size,
                    duration = it.duration,
                    artistName = it.artistName,
                    album = it.album,
                    albumThumbnailUri = it.albumThumbnailUri
                )
            }
            Result.Success(list)
        }

        is Result.Error -> {
            Result.Error(t)
        }
    }
}