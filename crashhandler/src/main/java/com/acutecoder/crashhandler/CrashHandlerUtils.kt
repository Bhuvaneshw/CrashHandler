package com.acutecoder.crashhandler

import android.content.Context
import com.acutecoder.crashhandler.helper.CrashCallback
import com.acutecoder.crashhandler.helper.DefaultErrorMessageFormatter
import com.acutecoder.crashhandler.helper.ErrorMessageFormatter
import com.acutecoder.crashhandler.helper.NotCrashHandlerInstanceException

fun CrashHandler.installCrashHandler(
    thread: Thread = Thread.currentThread(),
    messageFormatter: ErrorMessageFormatter = DefaultErrorMessageFormatter,
    callback: CrashCallback? = null,
    logToAndroidLogcat: Boolean = true,
) {
    initCrashHandler(thread, messageFormatter, callback, logToAndroidLogcat)
}

val Context.crashHandler: CrashHandler
    get() {
        if (applicationContext is CrashHandler)
            return applicationContext as CrashHandler
        else throw NotCrashHandlerInstanceException()
    }

var CrashHandler.needToShowLog: Boolean
    get() = crashPreference.getBoolean(Constants.KEY_NEED_TO_SHOW_LOG, false)
    set(value) {
        crashPreference.edit().putBoolean(Constants.KEY_NEED_TO_SHOW_LOG, value).apply()
    }

var CrashHandler.lastExceptionTime: String?
    get() = crashPreference.getString(Constants.KEY_LAST_EXCEPTION_TIME, null)
    set(value) {
        crashPreference.edit().putString(Constants.KEY_LAST_EXCEPTION_TIME, value).apply()
    }
