package com.example.mediaplayer3.domain

import android.content.Context
import com.example.mediaplayer3.data.entity.Result
import com.example.mediaplayer3.domain.entity.UiAudio
import com.example.mediaplayer3.repository.IAudioDataRepo
import com.example.mediaplayer3.repository.toUiAudio
import com.example.mplog.MPLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class FetchDataUseCase @Inject constructor(private val audioDataRepo: IAudioDataRepo ): IFetchDataUseCase,IUseCase {

    companion object{
        private const val CLASS_NAME = "FetchDataUseCase"
        private const val TAG = "AUDIO"
    }

    private var audioList: StateFlow<List<UiAudio>>? = null

    private val _scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val collectAudioList: IUseCaseJobScheduler by UseCaseScheduler{
        audioList?.collectLatest {
            MPLogger.d(CLASS_NAME,"collectAudioList", TAG,"list: $it")
        }
    }
    override val scope: CoroutineScope
        get() = _scope


    override fun requestData(
        context: Context,
        filterByName: (() -> String)?,
        filterByAlbum: (() -> String)?,
        filterByArtist: (() -> String)?
    ): Flow<List<UiAudio>> {
        return if (filterByName != null){
            collectAudioList.cancelJob()
            audioDataRepo.getSongsBySongName(context,filterByName()).map { value -> value.map { it.toUiAudio() } }.run {
                audioList = this.stateIn(scope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
                collectAudioList.launchJob()
                this.shareIn(scope, SharingStarted.WhileSubscribed())
            }
        }else if (filterByAlbum != null){
            audioDataRepo.getSongsByAlbum(context,filterByAlbum()).map { value -> value.map { it.toUiAudio() } }.run {
                audioList = this.stateIn(scope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
                collectAudioList.launchJob()
                this.shareIn(scope, SharingStarted.WhileSubscribed())
            }
        }else if (filterByArtist != null){
            audioDataRepo.getSongsByArtist(context,filterByArtist()).map { value -> value.map { it.toUiAudio() } }.run {
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

    override fun getSong(context: Context, id: Long): Flow<UiAudio?> {
        return audioDataRepo.getById(context, id).map { it?.toUiAudio() }
    }

    override fun getExtractedSongList(): List<UiAudio> {
        return audioList?.value?: emptyList()
    }

    override fun getSongListByPage(page: Int, pageSize: Int): Result<List<UiAudio>> {
        val staringIndex = page*pageSize
        return if (staringIndex + pageSize < (audioList?.value?.size ?: 0)){
            Result.Success(
                audioList?.value?.slice(staringIndex until staringIndex+pageSize) ?:run { emptyList() }
            )
        }else{
            Result.Success(emptyList())
        }
    }

    override fun observeLastPlayingSongId(context: Context): SharedFlow<Long> {
        return audioDataRepo.observeLastPlayingSong(context).shareIn(scope, started = SharingStarted.WhileSubscribed())
    }
}