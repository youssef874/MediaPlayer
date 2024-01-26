package com.example.mediaplayer3.viewModel

import androidx.lifecycle.viewModelScope
import com.example.mediaplayer3.domain.IAudioSyncUseCase
import com.example.mediaplayer3.viewModel.data.splash.SplashUiEvent
import com.example.mediaplayer3.viewModel.data.splash.SplashUiState
import com.example.mplog.MPLogger
import com.example.mpstorage.synchronizer.event.SynchronisationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val audioSyncUseCase: IAudioSyncUseCase,
): BaseViewModel<SplashUiEvent,SplashUiState>() {

    private val _uiState = MutableStateFlow(SplashUiState())
    override val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private var syncChangesObserver: Job

    init {

        handleEvents()

        syncChangesObserver = viewModelScope.launch {
            audioSyncUseCase.syncChanges.collectLatest {event->
                MPLogger.d(CLASS_NAME,"init", TAG,"synchronizationType: ${event.synchronizationType}")
                when(event.synchronizationType){
                    SynchronisationType.SYNCHRONISATION_STARTED->{
                        _uiState.update {
                            it.copy(isLoading = true, isSync = false, isFailed = false)
                        }
                    }
                    SynchronisationType.SYNCHRONISATION_COMPLETED->{
                        _uiState.update {
                            it.copy(isSync = true, isLoading = false, isFailed = false)
                        }
                    }
                    SynchronisationType.SYNCHRONIZATION_FAILED->{
                        _uiState.update {
                            it.copy(isFailed=true, isLoading = false, isSync = false)
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
                }
            }
        }
    }

    private fun handleSyncEvents(event: SplashUiEvent.Sync) {
        MPLogger.d(CLASS_NAME,"handleSyncEvents", TAG,"sync")
        audioSyncUseCase.sync(event.context)
    }

    override fun clear() {
        MPLogger.d(CLASS_NAME,"clear", TAG,"clear jobs")
        syncChangesObserver.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        MPLogger.d(CLASS_NAME,"onCleared", TAG,"this view model is cleared")
    }

    companion object{
        private const val CLASS_NAME = "SplashViewModel"
        private const val TAG = "SYNC"
    }
}