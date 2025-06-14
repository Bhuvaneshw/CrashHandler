package com.acutecoder.crashhandler.demo

import com.acutecoder.crashhandler.CrashHandlerApplication
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