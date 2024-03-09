package com.example.mplog

import com.example.mplog.data.BaseLogModule

/**
 * Structure to define how log is going to be written then printed in console
 */
interface ILogRuleProvider<T: BaseLogModule> {

    /**
     * This method where we going to define log rule
     * @param logModule: log message in module as you can write other than just simple method like class name, method name...
     * @return the message to print in the console
     */
    fun defineRuleForLog(logModule: T): String?
}