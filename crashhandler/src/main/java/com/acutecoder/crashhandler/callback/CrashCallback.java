package com.acutecoder.crashhandler.callback;

import org.jetbrains.annotations.NotNull;

public interface CrashCallback {
    void onCrash(@NotNull Throwable throwable);
}
