package com.acutecoder.crashhandler.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CrashLogger {
    void log(@Nullable String tag, @NotNull String message);
}
