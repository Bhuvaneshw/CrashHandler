package com.acutecoder.crashhandler.formatter;

import org.jetbrains.annotations.NotNull;

public interface ErrorMessageFormatter {
    @NotNull
    String format( @NotNull Throwable throwable);
}
