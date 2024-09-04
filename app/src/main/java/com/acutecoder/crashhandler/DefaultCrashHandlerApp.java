package com.acutecoder.crashhandler;

public class DefaultCrashHandlerApp extends CrashHandlerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initCrashHandler();

        //OR
        //initCrashHandler(Thread.currentThread(), DefaultErrorMessageFormatter.INSTANCE, new RestartAppCallback(this), new AndroidErrorLogger());
    }

}
