package com.acutecoder.crashhandler

import android.content.Intent
import com.acutecoder.crashhandler.helper.RestartAppCallback

class MyAppKt : CrashHandlerApplication() {

    init {
        installCrashHandler(callback = RestartAppCallback(this))
    }

    override fun startCrashHandlerActivity() {
        startActivity(Intent(this, CustomCrashHandlerActivity::class.java).apply {
            flags += Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

}