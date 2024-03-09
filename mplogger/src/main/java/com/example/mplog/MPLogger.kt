package com.example.mplog

import com.example.mplog.data.DefaultLogModule
import com.example.mplog.data.MPLogLevel
import com.example.mplog.data.MPLoggerProvider

object MPLogger:IMPLogger {

    private val defaultMPLoggerProvider = MPLoggerProvider.DefaultProvider.getLogger()

    init {
        defaultMPLoggerProvider.addClassToIgnore(MPLogger::class.java.name)
    }

    /**
     * Call this add class to ignore when computing the index of track trace when you write log using this library
     * like you need to ignore delegate classes that delegate this  if there any otherwise you dint need to call this function
     * `@throws: throw [IllegalArgumentException] if you you try to add a classes that already been added
     */
    fun addClassToIgnore(list: List<String>){
        list.forEach{
            defaultMPLoggerProvider.addClassToIgnore(it)
        }
    }


    override fun i(className: String, methodName: String, tag: String, msg: String) {
        defaultMPLoggerProvider.i(
            DefaultLogModule(
                className = className,
                methodName = methodName,
                tag = tag,
                msg = msg,
                MPLogLevel.INFO
            )
        )
    }

    override fun d(className: String, methodName: String, tag: String, msg: String) {
        defaultMPLoggerProvider.d(
            DefaultLogModule(
                className = className,
                methodName = methodName,
                tag = tag,
                msg = msg,
                MPLogLevel.DEBUG
            )
        )
    }

    override fun w(className: String, methodName: String, tag: String, msg: String) {
        defaultMPLoggerProvider.w(
            DefaultLogModule(
                className = className,
                methodName = methodName,
                tag = tag,
                msg = msg,
                MPLogLevel.WARNING
            )
        )
    }

    override fun e(className: String, methodName: String, tag: String, msg: String) {
        defaultMPLoggerProvider.e(
            DefaultLogModule(
                className = className,
                methodName = methodName,
                tag = tag,
                msg = msg,
                logLevel = MPLogLevel.ERROR
            )
        )
    }
}