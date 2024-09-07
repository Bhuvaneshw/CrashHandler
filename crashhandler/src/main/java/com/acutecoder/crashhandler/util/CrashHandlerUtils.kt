package com.acutecoder.crashhandler.util

import android.content.Context
import com.acutecoder.crashhandler.callback.CrashCallback
import com.acutecoder.crashhandler.core.CrashHandler
import com.acutecoder.crashhandler.core.NotCrashHandlerInstanceException
import com.acutecoder.crashhandler.formatter.DefaultErrorMessageFormatter
import com.acutecoder.crashhandler.formatter.ErrorMessageFormatter
import com.acutecoder.crashhandler.logger.AndroidErrorLogger
import com.acutecoder.crashhandler.logger.CrashLogger

fun CrashHandler.installCrashHandler(
    messageFormatter: ErrorMessageFormatter = DefaultErrorMessageFormatter,
    callback: CrashCallback? = null,
    logger: CrashLogger? = AndroidErrorLogger(),
    vararg threads: Thread,
) {
    initCrashHandler(messageFormatter, callback, logger, *threads)
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
