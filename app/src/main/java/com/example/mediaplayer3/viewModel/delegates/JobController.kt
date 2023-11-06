package com.example.mediaplayer3.viewModel.delegates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class JobController(
    val block: suspend (any: List<*>)->Unit
): IJobController, ReadWriteProperty<ViewModel, IJobController> {

    private var scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    override fun launchJob(vararg any: Any) {
        job = scope.launch {
            block(any.asList())
        }
    }

    override fun cancelJob() {
        job?.cancel()
        job = null
    }

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): IJobController {
        scope = thisRef.viewModelScope
        return this
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: IJobController) {
        scope = thisRef.viewModelScope
    }
}