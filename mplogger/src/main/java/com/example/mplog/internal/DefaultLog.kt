package com.example.mplog.internal

import android.util.Log
import com.example.mplog.ILog

internal object DefaultLog: ILog {
    override fun handleInfo(tag: String, msg: String) {
        Log.i(tag,msg)
    }

    override fun handleDebug(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    override fun handleWarning(tag: String, msg: String) {
        Log.w(tag, msg)
    }

    override fun handleError(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}