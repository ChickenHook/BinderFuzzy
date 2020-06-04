package org.chickenhook.binderfuzzy.utils

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.toFullString(): String {
    val sw = StringWriter()
    this.printStackTrace(PrintWriter(sw))
    val exceptionAsString: String = sw.toString()
    return exceptionAsString;
}