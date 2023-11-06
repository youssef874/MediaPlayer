package com.example.mplog

interface IMPLogger {

    fun i(
        className: String,
        methodName: String,
        tag: String,
        msg: String
    )

    fun d(
        className: String,
        methodName: String,
        tag: String,
        msg: String
    )

    fun w(
        className: String,
        methodName: String,
        tag: String,
        msg: String
    )

    fun e(
        className: String,
        methodName: String,
        tag: String,
        msg: String
    )
}