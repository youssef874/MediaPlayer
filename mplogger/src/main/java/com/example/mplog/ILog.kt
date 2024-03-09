package com.example.mplog

/**
 * Abstraction of printing message in console
 */
interface ILog {

    //Print messages as info
    fun handleInfo(tag: String,msg: String)

    //Print messages as debug
    fun handleDebug(tag: String,msg: String)

    //Print messages as warning
    fun handleWarning(tag: String,msg: String)

    //Print messages as error
    fun handleError(tag: String,msg: String)
}