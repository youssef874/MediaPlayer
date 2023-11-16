package com.example.mpeventhandler.internal.factory

import com.example.mpeventhandler.internal.IEventSubscriber

internal interface IEventSubscriberFactory {

    fun create(): IEventSubscriber
}