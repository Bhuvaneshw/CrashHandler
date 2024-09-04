package com.acutecoder.crashhandler.core

class NotCrashHandlerInstanceException :
    RuntimeException("Provided context.applicationContext is not an instance of CrashHandler")
