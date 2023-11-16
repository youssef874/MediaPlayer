package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mediaplayer3.repository.toUiAudio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

object FetchDataUseCase: IFetchDataUseCase,IUseCase {

    private lateinit var audioDataRepo: IAudioDataRepo
    private var _scope = CoroutineScope(Dispatchers.Default)

    private var audioList: StateFlow<List<UiAudio>>? = null

    private val collectAudioList: IUseCaseJobScheduler by UseCaseScheduler{
        audioList?.collect()
    }
    override val scope: CoroutineScope
        get() = _scope

    operator fun invoke(audioDataRepo: IAudioDataRepo, coroutineScope: CoroutineScope){
        this._scope = coroutineScope
        this.audioDataRepo = audioDataRepo
    }


    override fun requestData(
        context: Context,
        filterByName: (() -> String)?,
        filterByAlbum: (() -> String)?,
        filterByArtist: (() -> String)?
    ): Flow<List<UiAudio>> {
        return if (filterByName != null){
            audioDataRepo.getSongsBySongName(context,filterByName()).map { value -> value.map { it.toUiAudio() } }.run {
                collectAudioList.cancelJob()
                audioList = this.stateIn(scope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
                collectAudioList.launchJob()
                this.shareIn(scope, SharingStarted.WhileSubscribed())
            }
        }else if (filterByAlbum != null){
            audioDataRepo.getSongsByAlbum(context,filterByAlbum()).map { value -> value.map { it.toUiAudio() } }.run {
                collectAudioList.cancelJob()
                audioList = this.stateIn(scope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
                collectAudioList.launchJob()
                this.shareIn(scope, SharingStarted.WhileSubscribed())
            }
        }else if (filterByArtist != null){
            audioDataRepo.getSongsByArtist(context,filterByArtist()).map { value -> value.map { it.toUiAudio() } }.run {
                collectAudioList.cancelJob()
                audioList = this.stateIn(scope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
                collectAudioList.launchJob()
                this.shareIn(scope, SharingStarted.WhileSubscribed())
            }
        }else{
            audioDataRepo.getAllSong(context).map { value -> value.map { it.toUiAudio() } }.run {
                collectAudioList.cancelJob()
                audioList = this.stateIn(scope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
                collectAudioList.launchJob()
                this.shareIn(scope, SharingStarted.WhileSubscribed())
            }
        }
    }

    override fun getSong(context: Context, id: Long): Flow<UiAudio> {
        return audioDataRepo.getById(context, id).map { it.toUiAudio() }
    }

    override fun getExtractedSongList(): List<UiAudio> {
        return audioList?.value?: emptyList()
    }
}