package com.example.mpcore.api.log

import com.example.mpcore.internal.log.LogConfiguration
import com.example.mplog.MPLogger

object MPLog {

    private val mpLogger: MPLogger = LogConfiguration.builder()
        .addClassToIgnore(MPLog::class.java.name)
        .build()

    fun i(className: String, methodName: String, tag: String, msg: String){
        mpLogger.i(className, methodName, tag, msg)
    }

    fun d(className: String, methodName: String, tag: String, msg: String){
        mpLogger.d(className, methodName, tag, msg)
    }

    fun w(className: String, methodName: String, tag: String, msg: String){
        mpLogger.w(className, methodName, tag, msg)
    }

    fun e(className: String, methodName: String, tag: String, msg: String){
        mpLogger.e(className, methodName, tag, msg)
    }
}