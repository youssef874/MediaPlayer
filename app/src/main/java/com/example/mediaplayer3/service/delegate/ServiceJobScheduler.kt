package com.example.mediaplayer3.service.delegate

import com.example.mediaplayer3.service.IBaseService
import com.example.mediaplayer3.viewModel.delegates.IJobController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ServiceJobScheduler(
    private val block: suspend (any: List<*>)->Unit
): IJobController, ReadWriteProperty<IBaseService,IJobController> {

    private var scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null
    override fun launchJob(vararg any: Any) {
        job = scope.launch {
            block(any.asList())
        }
    }

    override fun cancelJob() {
        if (job?.isActive == true){
            job?.cancel()
        }
        job = null
    }

    override fun getValue(thisRef: IBaseService, property: KProperty<*>): IJobController {
        scope = thisRef.scope
        return this
    }

    override fun setValue(thisRef: IBaseService, property: KProperty<*>, value: IJobController) {
        scope = thisRef.scope
    }
}