package com.example.mplog.data

 data class DefaultLogModule(
    val className: String,
    val methodName: String,
    val tag: String,
    val msg: String,
    val logLevel: MPLogLevel
): BaseLogModule(msg,logLevel,tag)
