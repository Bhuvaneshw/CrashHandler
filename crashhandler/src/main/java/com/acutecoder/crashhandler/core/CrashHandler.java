package com.acutecoder.crashhandler.core;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Looper;

import com.acutecoder.crashhandler.callback.CrashCallback;
import com.acutecoder.crashhandler.formatter.DefaultErrorMessageFormatter;
import com.acutecoder.crashhandler.formatter.ErrorMessageFormatter;
import com.acutecoder.crashhandler.logger.AndroidErrorLogger;
import com.acutecoder.crashhandler.logger.CrashLogger;
import com.acutecoder.crashhandler.util.Constants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public interface CrashHandler {

    @NotNull
    File getCrashFile();

    @NotNull
    SharedPreferences getCrashPreference();

    default void initCrashHandler() {
        initCrashHandler(DefaultErrorMessageFormatter.INSTANCE, null, new AndroidErrorLogger());
    }

    default void initCrashHandler(@NotNull ErrorMessageFormatter messageFormatter, @Nullable CrashCallback callback, @Nullable CrashLogger logger, @Nullable Thread... threads) {
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, throwable) -> {
            onCatchThrowable(messageFormatter, t, throwable, logger);
            if (callback != null)
                callback.onCrash(throwable);
            System.exit(0);
        };

        if (threads != null && threads.length > 0) {
            for (Thread thread : threads) {
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }
        } else {
            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
    }

    default void onCatchThrowable(@NotNull ErrorMessageFormatter messageFormatter, @NotNull Thread thread, @NotNull Throwable throwable, @Nullable CrashLogger logger) {
        try {
            String message = messageFormatter.format(throwable);
            if (logger != null)
                logger.log("AndroidRuntime", message);
            onWriteThrowableMessage(getCrashFile(), message);
        } catch (IOException ignored) {
        }
    }

    default void onWriteThrowableMessage(@NotNull File file, @NotNull String message) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
            updatePreference();

            File folder = file.getParentFile();
            if (folder != null && !folder.exists())
                if (!folder.mkdirs()) return;
            if (!file.exists())
                if (!file.createNewFile()) return;

            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            out.println(message);
            out.println(getSeparator());
            out.println();

            out.close();
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception ignored) {
            }
            try {
                if (bw != null) bw.close();
            } catch (IOException ignored) {
            }
            try {
                if (fw != null) fw.close();
            } catch (IOException ignored) {
            }
        }
    }

    @NotNull
    default ErrorLog loadErrorLog() throws ReadLogOnMainThreadException {
        if (Looper.myLooper() == Looper.getMainLooper())
            throw new ReadLogOnMainThreadException();

        if (!getCrashFile().exists()) return new ErrorLog(null, null);

        List<String> errors = new ArrayList<>();
        StringBuilder plainError = new StringBuilder();
        try (
                FileInputStream fs = new FileInputStream(getCrashFile());
                InputStreamReader in = new InputStreamReader(fs);
                BufferedReader reader = new BufferedReader(in)
        ) {
            String line = reader.readLine();
            while (line != null) {
                if (plainError.length() > 0)
                    plainError.append("\n");
                plainError.append(line);
                line = reader.readLine();
            }

            String[] errs = plainError.toString().split(getSeparator());
            for (String err : errs) {
                if (!err.trim().isEmpty())
                    errors.add(err.trim());
            }
        } catch (IOException ignored) {
        }

        return new ErrorLog(Collections.unmodifiableList(errors), getCrashPreference().getString(Constants.KEY_LAST_EXCEPTION_TIME, null));
    }

    @SuppressLint("ApplySharedPref")
    default void updatePreference() {
        SharedPreferences.Editor editor = getCrashPreference().edit();
        editor.putBoolean(Constants.KEY_NEED_TO_SHOW_LOG, true);
        editor.putString(Constants.KEY_LAST_EXCEPTION_TIME, getCurrentDateAndTime());
        editor.commit();
    }

    @SuppressLint("ApplySharedPref")
    default boolean clearAll() {
        SharedPreferences.Editor editor = getCrashPreference().edit();
        editor.putBoolean(Constants.KEY_NEED_TO_SHOW_LOG, false);
        editor.putString(Constants.KEY_LAST_EXCEPTION_TIME, null);
        editor.commit();
        try {
            return getCrashFile().delete();
        } catch (Exception ignored) {
        }
        return false;
    }

    @NotNull
    default String getCurrentDateAndTime() {
        Calendar instance = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
        return formatter.format(instance.getTime());
    }

    @NotNull
    default String getSeparator() {
        return Constants.SEPARATOR;
    }

}
