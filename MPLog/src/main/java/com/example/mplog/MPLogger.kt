package com.example.mplog

import com.example.mplog.data.MPLogLevel
import com.example.mplog.internal.ILogWriter
import com.example.mplog.internal.LogWriterImpl

object MPLogger:IMPLogger {

    private val logWriterImpl: ILogWriter =  LogWriterImpl()

    init {
        logWriterImpl.addClassToIgnoreInLogger(MPLogger::class.java.name)
    }


    override fun i(className: String, methodName: String, tag: String, msg: String) {
        logWriterImpl.log(MPLogLevel.INFO,className, methodName, tag, msg)
    }

    override fun d(className: String, methodName: String, tag: String, msg: String) {
        logWriterImpl.log(MPLogLevel.DEBUG,className, methodName, tag, msg)
    }

    override fun w(className: String, methodName: String, tag: String, msg: String) {
        logWriterImpl.log(MPLogLevel.WARNING,className, methodName, tag, msg)
    }

    override fun e(className: String, methodName: String, tag: String, msg: String) {
        logWriterImpl.log(MPLogLevel.ERROR,className, methodName, tag, msg)
    }
}