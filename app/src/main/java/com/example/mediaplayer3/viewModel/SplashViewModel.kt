package com.example.mediaplayer3.viewModel

import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.domain.AudioSyncUseCase
import com.example.mediaplayer3.domain.FetchDataUseCase
import com.example.mediaplayer3.domain.IAudioSyncUseCase
import com.example.mediaplayer3.domain.IFetchDataUseCase
import com.example.mediaplayer3.viewModel.data.splash.SplashUiEvent
import com.example.mediaplayer3.viewModel.data.splash.SplashUiState
import com.example.mplog.MPLogger
import com.example.mpstorage.synchronizer.event.SynchronisationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val audioSyncUseCase: IAudioSyncUseCase = AudioSyncUseCase,
    private val fetchDataUseCase: IFetchDataUseCase = FetchDataUseCase
): BaseViewModel<SplashUiEvent,SplashUiState>() {

    private val _uiState = MutableStateFlow(SplashUiState())
    override val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        (audioSyncUseCase as AudioSyncUseCase).invoke(getAudioDataRepo(),viewModelScope)
        (fetchDataUseCase as FetchDataUseCase).invoke(getAudioDataRepo(),viewModelScope)

        handleEvents()

        viewModelScope.launch {
            audioSyncUseCase.syncChanges.collectLatest {
                MPLogger.d(CLASS_NAME,"init", TAG,"synchronizationType: ${it.synchronizationType}")
                when(it.synchronizationType){
                    SynchronisationType.SYNCHRONISATION_STARTED->{
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                    SynchronisationType.SYNCHRONISATION_COMPLETED->{
                        _uiState.update {
                            it.copy(isSync = true)
                        }
                    }
                    SynchronisationType.SYNCHRONIZATION_FAILED->{
                        _uiState.update {
                            it.copy(isFailed=true, isLoading = false)
                        }
                    }
                }
            }
        }
    }

    private fun handleEvents() {
        viewModelScope.launch {
            uiEvent.collectLatest {
                MPLogger.d(CLASS_NAME,"handleEvents", TAG,"event: $it")
                when(it){
                    is SplashUiEvent.Sync->{
                        handleSyncEvents(it)
                    }
                    is SplashUiEvent.RequestData->{
                        handleRequestDataEvent(it)
                    }
                }
            }
        }
    }

    private fun handleRequestDataEvent(event: SplashUiEvent.RequestData) {
        MPLogger.i(CLASS_NAME,"handleRequestDataEvent", TAG,"request data")
        viewModelScope.launch {
            fetchDataUseCase.requestData(event.context).onEach {
                _uiState.update {
                    it.copy(isSuccess = true, isLoading = false, isSync = false, isFailed = false)
                }
            }.catch {
                _uiState.update {
                    it.copy(isFailed = true, isSync = false, isLoading = false, isSuccess = false)
                }
            }
        }
    }

    private fun handleSyncEvents(event: SplashUiEvent.Sync) {
        MPLogger.d(CLASS_NAME,"handleSyncEvents", TAG,"sync")
        audioSyncUseCase.sync(event.context)
    }

    companion object{
        private const val CLASS_NAME = "SplashViewModel"
        private const val TAG = "SYNC"
    }
}