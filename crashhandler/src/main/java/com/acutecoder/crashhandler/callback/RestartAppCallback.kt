package com.acutecoder.crashhandler.callback

import android.content.Context
import android.content.Intent

class RestartAppCallback(private val context: Context) : CrashCallback {

    override fun onCrash(throwable: Throwable) {
        val intent =
            context.applicationContext.packageManager.getLaunchIntentForPackage(context.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
        context.startActivity(mainIntent)
    }

}