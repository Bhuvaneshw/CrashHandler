package com.acutecoder.crashhandler.formatter

import java.io.PrintWriter
import java.io.StringWriter

object DefaultErrorMessageFormatter : ErrorMessageFormatter {

    override fun format(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        return stringWriter.toString()
    }

}
