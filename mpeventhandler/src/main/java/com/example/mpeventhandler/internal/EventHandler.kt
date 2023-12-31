package com.example.mpeventhandler.internal

import com.example.mpeventhandler.data.MPEvent

internal class EventHandler(
    inline val filter: (MPEvent)->Boolean,
    listener: MPEventListener
): IEventHandler {

    private var _listener: MPEventListener? = listener
    override val listener: MPEventListener?
        get() = _listener

    override var onEventDelivered: (() -> Unit)? = null
    override var onEventCanceled: (() -> Unit)?  = null

    private var _isDisposed = false
    override val isDisposed: Boolean
        get() = _isDisposed

    override fun dispose() {
        _listener = null
        _isDisposed = true
        onEventCanceled?.invoke()
    }

    override fun onNext(event: MPEvent) {
        check(!isDisposed){"Subscription is already disposed there fore subscribe should not called"}
        if (filter(event)){
            try {
                listener?.onEvent(event)
            }finally {
                onEventDelivered?.invoke()
            }
        }
    }
}