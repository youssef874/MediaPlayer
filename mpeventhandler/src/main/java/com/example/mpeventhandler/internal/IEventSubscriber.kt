package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent

internal interface IEventSubscriber {

    fun onNext(event: MPEvent): Boolean

    fun  subscribe(events: Array<MPEvent>, listener: MPEventListener): IEventCanceler

    fun oneShotSubscriber(events: Array<MPEvent>, listener: MPEventListener)

    suspend fun  subscribeAsync(events: Array<MPEvent>, listener: MPEventListener)
}