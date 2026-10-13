package com.example.app.core

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WorkerLogger {

    private const val FILE_NAME = "worker_log.txt"

    fun log(context: Context, tag: String, message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        val line = "[$timestamp] [$tag] $message\n"
        File(context.getExternalFilesDir(null), FILE_NAME).appendText(line)
    }

    fun logError(context: Context, tag: String, throwable: Throwable) {
        val sw = java.io.StringWriter()
        throwable.printStackTrace(java.io.PrintWriter(sw))
        log(context, tag, "ERROR:\n$sw")
    }

    fun read(context: Context): String {
        val file = File(context.getExternalFilesDir(null), FILE_NAME)
        return if (file.exists()) file.readText() else "(no worker log)"
    }

    fun clear(context: Context) {
        File(context.getExternalFilesDir(null), FILE_NAME).delete()
    }
}