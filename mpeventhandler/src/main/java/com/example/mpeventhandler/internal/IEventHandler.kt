package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent

internal interface IEventHandler: IEventCanceler {

    val listener: MPEventListener?

    var onEventDelivered: (()->Unit)?

    fun onNext(event: MPEvent)

}

interface IEventCanceler{

    val isDisposed: Boolean

    fun dispose()
}

interface MPEventListener{

    fun onEvent(event:MPEvent)
}