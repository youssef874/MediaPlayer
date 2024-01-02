package com.example.mplog.internal

import android.util.Log
import com.example.mplog.data.MPLogLevel
import java.util.logging.Logger




internal class LogWriterImpl: ILogWriter {

    private val ignoredClassList = mutableListOf<String>()

    override fun log(
        mpLogLevel: MPLogLevel,
        className: String,
        methodName: String,
        tag: String,
        msg: String
    ) {

        val trace = Thread.currentThread().getStackTrace()
        val index =getStackOffset(trace) + 1
        if (index >= trace.size){
            return
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(className)
        stringBuilder.append(" | ")
        stringBuilder.append(methodName)
        stringBuilder.append(" | ")
        stringBuilder.append(tag)
        stringBuilder.append(" | ")
        stringBuilder.append(mpLogLevel.description)
        stringBuilder.append(" | ")
        stringBuilder.append("[")
        stringBuilder.append(trace[index].fileName)
        stringBuilder.append(":")
        stringBuilder.append(trace[index].lineNumber)
        stringBuilder.append("]")
        stringBuilder.append(trace[index].methodName)
        stringBuilder.append(" | ")
        stringBuilder.append(msg)
        doLog(mpLogLevel, stringBuilder.toString())

    }

    override fun addClassToIgnoreInLogger(className: String) {
        ignoredClassList.add(className)
    }

    private fun doLog(mpLogLevel: MPLogLevel, msg: String){
        when(mpLogLevel){
            MPLogLevel.DEBUG->Log.d(mpLogLevel.description,msg)
            MPLogLevel.INFO->Log.i(mpLogLevel.description,msg)
            MPLogLevel.WARNING->Log.w(mpLogLevel.description,msg)
            MPLogLevel.ERROR->Log.e(mpLogLevel.description,msg)
            MPLogLevel.NONE->return
        }
    }

    private fun getStackOffset(
        trace: Array<StackTraceElement>
    ): Int {
        var i = 3
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != LogWriterImpl::class.java.name && name != Logger::class.java.name && !isContainIgnored(name)
            ) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun isContainIgnored(name: String): Boolean{
        if (name.isEmpty()){
            return false
        }
        if (ignoredClassList.isEmpty()){
            return false
        }else{
            ignoredClassList.forEach {
                if (it == name){
                    return true
                }
            }
        }
        return false
    }
}