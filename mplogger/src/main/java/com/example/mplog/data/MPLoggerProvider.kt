package com.example.mplog.data

import com.example.mplog.internal.DefaultLog
import com.example.mplog.internal.DefaultLoggerRuleProvider
import com.example.mplog.ILog
import com.example.mplog.ILogRuleProvider
import com.example.mplog.IMPLog
import java.lang.IllegalStateException

class MPLoggerProvider<T: BaseLogModule> private constructor(
    private val loggerRuleProvider: ILogRuleProvider<T>,
    private val iLog: ILog
): IMPLog<T>{

    class DefaultProvider private constructor(): IMPLog<DefaultLogModule>{
        private val defaultLoggerRuleProvider = DefaultLoggerRuleProvider()

        private val defaultLog = DefaultLog

        init {
            defaultLoggerRuleProvider.addClassToIgnore(DefaultProvider::class.java.name)
        }

        fun addClassToIgnore(className: String): DefaultProvider{
            defaultLoggerRuleProvider.addClassToIgnore(className)
            return this
        }

        override fun i(log: DefaultLogModule) {
            if (log.logLevel != MPLogLevel.INFO){
                throw IllegalStateException("The log level need to bee in info")
            }
            val msg = defaultLoggerRuleProvider.defineRuleForLog(log)
            msg?.let { defaultLog.handleInfo(log.tag, it) }
        }

        override fun d(log: DefaultLogModule) {
            if (log.logLevel != MPLogLevel.DEBUG){
                throw IllegalStateException("The log level need to bee in debug")
            }
            val msg = defaultLoggerRuleProvider.defineRuleForLog(log)
            msg?.let { defaultLog.handleDebug(log.tag, it) }
        }

        override fun w(log: DefaultLogModule) {
            if (log.logLevel != MPLogLevel.WARNING){
                throw IllegalStateException("The log level need to bee in warning")
            }
            val msg = defaultLoggerRuleProvider.defineRuleForLog(log)
            msg?.let { defaultLog.handleWarning(log.tag, it) }
        }

        override fun e(log: DefaultLogModule) {
            if (log.logLevel != MPLogLevel.ERROR){
                throw IllegalStateException("The log level need to bee in error")
            }
            val msg = defaultLoggerRuleProvider.defineRuleForLog(log)
            msg?.let { defaultLog.handleError(log.tag, it) }
        }

        companion object{
            fun getLogger(): DefaultProvider = DefaultProvider().addClassToIgnore(MPLoggerProvider::class.java.name)
        }

    }

    override fun i(log: T) {
        if (log.level != MPLogLevel.INFO){
            throw IllegalStateException("The log level need to bee in info")
        }
        val msg = loggerRuleProvider.defineRuleForLog(log)
        msg?.let { iLog.handleInfo(log.logTag, it) }
    }

    override fun d(log: T) {
        if (log.level != MPLogLevel.DEBUG){
            throw IllegalStateException("The log level need to bee in debug")
        }
        val msg = loggerRuleProvider.defineRuleForLog(log)
        msg?.let { iLog.handleDebug(log.logTag, it) }
    }

    override fun w(log: T) {
        if (log.level != MPLogLevel.WARNING){
            throw IllegalStateException("The log level need to bee in warning")
        }
        val msg = loggerRuleProvider.defineRuleForLog(log)
        msg?.let { iLog.handleWarning(log.logTag, it) }
    }

    override fun e(log: T) {
        if (log.level != MPLogLevel.ERROR){
            throw IllegalStateException("The log level need to bee in error")
        }
        val msg = loggerRuleProvider.defineRuleForLog(log)
        msg?.let { iLog.handleError(log.logTag, it) }
    }
}