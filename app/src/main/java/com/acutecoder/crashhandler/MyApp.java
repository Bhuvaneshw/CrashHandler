package com.acutecoder.crashhandler;

public class MyApp extends CrashHandlerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initCrashHandler();

        //OR
        //initCrashHandlerWith(Thread.currentThread(), DefaultErrorMessageFormatter.INSTANCE, null, true);
    }

}
