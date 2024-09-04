package com.acutecoder.crashhandler.logger

import android.util.Log

class AndroidErrorLogger : CrashLogger {

    override fun log(tag: String?, message: String) {
        Log.e(tag, message)
    }

}
