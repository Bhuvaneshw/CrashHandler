package com.acutecoder.crashhandler.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ErrorLog {

    private final List<String> errors;
    private final String lastErrorTime;

    public ErrorLog(@Nullable List<String> errors, @Nullable String lastErrorTime) {
        this.errors = errors;
        this.lastErrorTime = lastErrorTime;
    }

    @Nullable
    public List<String> getErrors() {
        return errors;
    }

    @Nullable
    public String getLastErrorTime() {
        return lastErrorTime;
    }

    @NotNull
    public String simplifiedLog() {
        if (errors == null || errors.isEmpty()) return "No error logs found!";

        switch (errors.size()) {
            case 1:
                return errors.get(0);
            case 2:
                return errors.get(0) + "\n\n and 1 more error";
            default:
                return errors.get(0) + "\n\n and " + (errors.size() - 1) + " more errors";
        }
    }
}
