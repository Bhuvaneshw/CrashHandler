package com.acutecoder.crashhandler

import com.acutecoder.crashhandler.callback.RestartAppCallback
import com.acutecoder.crashhandler.util.installCrashHandler
import com.acutecoder.crashhandler.util.timedErrorMessageFormatter

class CustomCrashHandlerApp : CrashHandlerApplication() {

    init {
        installCrashHandler(
            errorMessageFormatter = timedErrorMessageFormatter(),
            callback = RestartAppCallback(this)
        )

        //OR
        // installCrashHandler(
        //    errorMessageFormatter = DefaultErrorMessageFormatter,
        //    callback = null,
        //    logger = AndroidErrorLogger(),
        //)
    }

    override fun startCrashHandlerActivity(defaultActivityClass: Class<*>) {
        super.startCrashHandlerActivity(CustomCrashHandlerActivity::class.java)
    }

}