package com.example.mediaplayer3.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mplog.MPLogger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<EVENT,STATE>: ViewModel() {


    abstract val uiState: StateFlow<STATE>

    private val _uiEvent = MutableSharedFlow<EVENT>()
    protected val uiEvent: SharedFlow<EVENT> = _uiEvent.asSharedFlow()

    fun onEvent(event: EVENT){
        MPLogger.d(CLASS_NAME,"onEvent", TAG,"event: $event")
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    abstract fun clear()

    companion object{
        private const val CLASS_NAME = "BaseViewModel"
        private const val TAG = "APPLICATION"
    }
}