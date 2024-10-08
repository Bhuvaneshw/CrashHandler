package com.acutecoder.crashhandler

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import com.acutecoder.crashhandler.core.CrashHandler
import com.acutecoder.crashhandler.util.crashHandler
import com.acutecoder.crashhandler.util.installCrashHandler
import com.acutecoder.crashhandler.util.needToShowLog
import java.io.File

open class SomeOtherApplication : Application()

class OtherApplicationExample : SomeOtherApplication(), CrashHandler {

    init {
        installCrashHandler()
        if (crashHandler.needToShowLog) {
            startActivity(Intent(this, CrashHandlerActivity::class.java).apply {
                flags += Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    override fun getCrashFile(): File = File(filesDir, "my_crash.txt")

    override fun getCrashPreference(): SharedPreferences =
        getSharedPreferences("my_crash_preference", MODE_PRIVATE)

}
