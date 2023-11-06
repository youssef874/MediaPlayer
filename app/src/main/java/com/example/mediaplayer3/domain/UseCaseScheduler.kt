package com.example.mediaplayer3.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UseCaseScheduler(private val bloc: suspend (List<Any>)->Unit): ReadWriteProperty<IUseCase,IUseCaseJobScheduler>, IUseCaseJobScheduler {

    private var _scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    override fun getValue(thisRef: IUseCase, property: KProperty<*>): IUseCaseJobScheduler {
        _scope = thisRef.scope
        return this
    }

    override fun setValue(thisRef: IUseCase, property: KProperty<*>, value: IUseCaseJobScheduler) {
        _scope = thisRef.scope
    }

    override fun launchJob(vararg any: Any) {
        job = _scope.launch {
            bloc(any.asList())
        }
    }

    override fun cancelJob() {
       job?.cancel()
        job = null
    }
}