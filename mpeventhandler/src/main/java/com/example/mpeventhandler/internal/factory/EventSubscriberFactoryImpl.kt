package com.example.mpeventhandler.internal.factory

import com.example.mpeventhandler.internal.EventSubscribeImpl
import com.example.mpeventhandler.internal.IEventSubscriber

internal object EventSubscriberFactoryImpl: IEventSubscriberFactory {

    private var sInstance: IEventSubscriber? = null

    override fun create(): IEventSubscriber {
        return sInstance?: synchronized(this){
            val instance = EventSubscribeImpl
            sInstance = instance
            instance
        }
    }
}