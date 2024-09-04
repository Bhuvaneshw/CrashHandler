package com.acutecoder.crashhandler

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import com.acutecoder.crashhandler.core.CrashHandler
import com.acutecoder.crashhandler.util.Constants
import com.acutecoder.crashhandler.util.crashHandler
import com.acutecoder.crashhandler.util.needToShowLog
import java.io.File

open class CrashHandlerApplication : Application(),
    CrashHandler {

    override fun onCreate() {
        super.onCreate()
        if (crashHandler.needToShowLog)
            startCrashHandlerActivity(CrashHandlerActivity::class.java)
    }

    protected open fun startCrashHandlerActivity(defaultActivityClass: Class<*>) {
        startActivity(defaultActivityClass)
    }

    private fun startActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass).apply {
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
