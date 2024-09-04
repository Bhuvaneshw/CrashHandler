package com.acutecoder.crashhandler;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;

import com.acutecoder.crashhandler.helper.AndroidErrorLogger;
import com.acutecoder.crashhandler.helper.CrashCallback;
import com.acutecoder.crashhandler.helper.DefaultErrorMessageFormatter;
import com.acutecoder.crashhandler.helper.ErrorLog;
import com.acutecoder.crashhandler.helper.ErrorMessageFormatter;
import com.acutecoder.crashhandler.helper.CrashLogger;
import com.acutecoder.crashhandler.helper.ReadLogOnMainThreadException;

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
import java.util.List;
import java.util.Locale;

public interface CrashHandler {

    @NotNull
    File getCrashFile();

    @NotNull
    SharedPreferences getCrashPreference();

    default void initCrashHandler() {
        initCrashHandler(Thread.currentThread(), DefaultErrorMessageFormatter.INSTANCE, null, new AndroidErrorLogger());
    }

    default void initCrashHandler(@NotNull Thread thread, @NotNull ErrorMessageFormatter messageFormatter, @Nullable CrashCallback callback, @Nullable CrashLogger logger) {
        thread.setUncaughtExceptionHandler((t, throwable) -> {
                    onCatchThrowable(messageFormatter, t, throwable, logger);
                    if (callback != null)
                        callback.onCrash(throwable);
                    System.exit(0);
                }
        );
    }

    default void onCatchThrowable(@NotNull ErrorMessageFormatter messageFormatter, @NotNull Thread thread, @NotNull Throwable throwable, @Nullable CrashLogger logger) {
        try {
            if (messageFormatter == DefaultErrorMessageFormatter.INSTANCE) {
                String message = messageFormatter.format(throwable);
                if (logger != null)
                    logger.log("AndroidRuntime", message);
                onWriteThrowableMessage(getCrashFile(), message);
            } else {
                if (logger != null)
                    logger.log("AndroidRuntime", DefaultErrorMessageFormatter.INSTANCE.format(throwable));
                onWriteThrowableMessage(getCrashFile(), messageFormatter.format(throwable));
            }
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

            out.println(getSeparator());
            out.println(message);

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

        return new ErrorLog(errors, getCrashPreference().getString(Constants.KEY_LAST_EXCEPTION_TIME, null));
    }

    @SuppressLint("ApplySharedPref")
    default void updatePreference() {
        SharedPreferences.Editor editor = getCrashPreference().edit();
        editor.putBoolean(Constants.KEY_NEED_TO_SHOW_LOG, true);
        editor.putString(Constants.KEY_LAST_EXCEPTION_TIME, getCurrentDateAndTime());
        editor.commit();
        Log.e("preference", getCrashPreference().getBoolean(Constants.KEY_NEED_TO_SHOW_LOG, false) + "");
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
