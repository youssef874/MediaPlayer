package com.example.mplog.internal

import com.example.mplog.data.MPLogLevel

internal interface ILogWriter {

    fun log(mpLogLevel: MPLogLevel,className: String, methodName: String, tag: String,msg: String)

    fun addClassToIgnoreInLogger(className: String)
}