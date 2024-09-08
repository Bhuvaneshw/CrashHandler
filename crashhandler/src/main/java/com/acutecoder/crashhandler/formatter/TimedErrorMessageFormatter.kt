package com.acutecoder.crashhandler.formatter

import com.acutecoder.crashhandler.core.CurrentDateTimeProvider

class TimedErrorMessageFormatter(
    private val dateTimeProvider: CurrentDateTimeProvider,
    private val errorMessageFormatter: ErrorMessageFormatter = DefaultErrorMessageFormatter,
) : ErrorMessageFormatter {

    override fun format(throwable: Throwable): String {
        return "Time: ${dateTimeProvider.currentDateAndTime}\n" +
                errorMessageFormatter.format(throwable)
    }

}
