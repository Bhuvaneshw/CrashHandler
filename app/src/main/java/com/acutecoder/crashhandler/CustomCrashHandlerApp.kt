package com.acutecoder.crashhandler

import com.acutecoder.crashhandler.helper.RestartAppCallback

class CustomCrashHandlerApp : CrashHandlerApplication() {

    init {
        installCrashHandler(callback = RestartAppCallback(this))
    }

    override fun startCrashHandlerActivity(defaultActivityClass: Class<*>) {
        super.startCrashHandlerActivity(CustomCrashHandlerActivity::class.java)
    }

}