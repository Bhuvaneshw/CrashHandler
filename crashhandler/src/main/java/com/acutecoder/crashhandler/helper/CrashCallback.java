package com.acutecoder.crashhandler.helper;

import org.jetbrains.annotations.NotNull;

public interface CrashCallback {
    void onCrash(@NotNull Throwable throwable);
}
