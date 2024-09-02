package com.acutecoder.crashhandler.helper

class NotCrashHandlerInstanceException :
    RuntimeException("Provided context.applicationContext is not an instance of CrashHandler")
