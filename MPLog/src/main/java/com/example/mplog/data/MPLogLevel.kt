package com.example.mplog.data

internal enum class MPLogLevel(val level: Int, val description: String) {

    DEBUG(1,"DEBUG"),
    INFO(2,"INFO"),
    WARNING(3,"INFO"),
    ERROR(4,"ERROR"),
    NONE(99,"NONE");

    fun getMPLoggerWithLevel(level: Int): MPLogLevel {
        return when(level){
            DEBUG.level->DEBUG
            INFO.level->INFO
            WARNING.level->WARNING
            ERROR.level->ERROR
            else->NONE
        }
    }
}