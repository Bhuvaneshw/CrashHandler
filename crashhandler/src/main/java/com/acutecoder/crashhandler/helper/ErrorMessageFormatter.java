package com.acutecoder.crashhandler.helper;

import org.jetbrains.annotations.NotNull;

public interface ErrorMessageFormatter {
    @NotNull
    String format( @NotNull Throwable throwable);
}
