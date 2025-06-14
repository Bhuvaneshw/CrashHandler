package com.acutecoder.crashhandler.demo;

import com.acutecoder.crashhandler.CrashHandlerApplication;

public class DefaultCrashHandlerApp extends CrashHandlerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initCrashHandler();

        //OR
        //initCrashHandler(DefaultErrorMessageFormatter.INSTANCE, new RestartAppCallback(this), new AndroidErrorLogger());
    }

}
