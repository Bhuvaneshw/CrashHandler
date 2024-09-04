package com.acutecoder.crashhandler

import com.acutecoder.crashhandler.callback.RestartAppCallback
import com.acutecoder.crashhandler.util.installCrashHandler

class CustomCrashHandlerApp : CrashHandlerApplication() {

    init {
        installCrashHandler(callback = RestartAppCallback(this))
    }

    override fun startCrashHandlerActivity(defaultActivityClass: Class<*>) {
        super.startCrashHandlerActivity(CustomCrashHandlerActivity::class.java)
    }

}