package com.example.mediaplayer3.viewModel.delegates

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias fun1 = suspend (Context)->Unit
class AudioProgressJob (var func: fun1): ReadWriteProperty<ViewModel, IAudioProgressJob>,
    IAudioProgressJob {
    private var scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): IAudioProgressJob {
        scope = thisRef.viewModelScope
        return this
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: IAudioProgressJob) {
        scope = thisRef.viewModelScope
    }

    override fun launchJob(context: Context) {
        job = scope.launch {
            Log.d("AudioProgressJob", "[launchJob]")
            func(context)
        }
    }

    override fun cancelJob() {
        Log.d("AudioProgressJob", "[cancelJob]")
        job?.cancel()
        job = null
    }

}