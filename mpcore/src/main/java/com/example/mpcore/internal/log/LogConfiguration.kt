package com.example.mpcore.internal.log

import com.example.mplog.MPLogger

internal class LogConfiguration private constructor(){

    private val classesToIgnore = mutableListOf<String>()

    private val mpLogger = MPLogger

    fun addClassToIgnore(className: String): LogConfiguration{
        try {
            classesToIgnore.add(className)
            mpLogger.addClassToIgnore(classesToIgnore)
        }catch (e: IllegalArgumentException){
            if (classesToIgnore.contains(className)){
                classesToIgnore.remove(className)
            }
        }
        return this
    }

    fun build(): MPLogger{
        return mpLogger
    }

    companion object{

        @Volatile
        private var INSTANCE: LogConfiguration? = null

        fun builder(): LogConfiguration{
            return INSTANCE?: synchronized(this){
                val instance = LogConfiguration()
                INSTANCE = instance
                instance
            }
        }
    }
}