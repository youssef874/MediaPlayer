package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent
import kotlinx.coroutines.flow.Flow

internal interface IEventSubscriber {

    fun onNext(event: MPEvent): Boolean

    fun  subscribe(events: Array<MPEvent>, listener: MPEventListener): IEventCanceler

    fun oneShotSubscriber(events: Array<MPEvent>, listener: MPEventListener)

    fun collectEvent(events: Array<String>): Flow<MPEvent>
}