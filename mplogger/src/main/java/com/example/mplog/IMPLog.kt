package com.example.mplog

import com.example.mplog.data.BaseLogModule

interface IMPLog<T: BaseLogModule> {

    fun i(log: T)

    fun d(log: T)

    fun w(log: T)

    fun e(log: T)
}