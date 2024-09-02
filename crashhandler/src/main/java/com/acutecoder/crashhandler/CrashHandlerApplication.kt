package com.acutecoder.crashhandler

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import java.io.File

open class CrashHandlerApplication : Application(), CrashHandler {

    override fun onCreate() {
        super.onCreate()
        if (crashHandler.needToShowLog.apply { Log.e("app", "Need to show $this") })
            startCrashHandlerActivity()
    }

    protected open fun startCrashHandlerActivity() {
        startActivity(Intent(this, CrashHandlerActivity::class.java).apply {
            flags += Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    override fun getCrashFile(): File {
        return File(filesDir, Constants.CRASH_FILE_NAME)
    }

    override fun getCrashPreference(): SharedPreferences {
        return getSharedPreferences("crash_preference", MODE_PRIVATE)
    }
}
