package com.example.mplog.internal

import com.example.mplog.ILogRuleProvider
import com.example.mplog.data.DefaultLogModule

/**
 * Default implementation for [ILogRuleProvider] ,which will define clickable link
 * to the log you written
 */
internal class DefaultLoggerRuleProvider: ILogRuleProvider<DefaultLogModule> {

    private val _classesToIgnore = mutableListOf<String>()

    fun addClassToIgnore(className: String){
        _classesToIgnore.add(className)
    }

    init {
        addClassToIgnore(DefaultLoggerRuleProvider::class.java.name)
    }

    override fun defineRuleForLog(logModule: DefaultLogModule): String? {
        val trace = Thread.currentThread().getStackTrace()
        val index =getStackOffset(trace) + 1
        if (index >= trace.size){
            return null
        }
        val stringBuilder = StringBuilder()
        stringBuilder.append(logModule.className)
        stringBuilder.append(" | ")
        stringBuilder.append(logModule.methodName)
        stringBuilder.append(" | ")
        stringBuilder.append(logModule.tag)
        stringBuilder.append(" | ")
        stringBuilder.append(logModule.logLevel.description)
        stringBuilder.append(" | ")
        stringBuilder.append("[")
        stringBuilder.append(trace[index].fileName)
        stringBuilder.append(":")
        stringBuilder.append(trace[index].lineNumber)
        stringBuilder.append("]")
        stringBuilder.append(trace[index].methodName)
        stringBuilder.append(" | ")
        stringBuilder.append(logModule.logMsg)
        return stringBuilder.toString()
    }

    private fun getStackOffset(
        trace: Array<StackTraceElement>
    ): Int{
        var i = 3
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if ( !isContainIgnored(name)
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
        if (_classesToIgnore.isEmpty()){
            return false
        }else{
            _classesToIgnore.forEach {
                if (it == name){
                    return true
                }
            }
        }
        return false
    }
}