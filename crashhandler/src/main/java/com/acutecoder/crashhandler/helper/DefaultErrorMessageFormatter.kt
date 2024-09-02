package com.acutecoder.crashhandler.helper

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
