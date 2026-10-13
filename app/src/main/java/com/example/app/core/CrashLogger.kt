package com.example.app.core

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ghi crash log ra internal storage.
 * Đọc file bằng File Manager (nếu root) hoặc mở qua app đọc file.
 */
object CrashLogger {

    private const val FILE_NAME = "crash_log.txt"

    fun install(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                writeCrashLog(context, thread, throwable)
            } catch (_: Throwable) {
                // best-effort
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashLog(context: Context, thread: Thread, throwable: Throwable) {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val info = buildString {
            appendLine("=".repeat(60))
            appendLine("TIME: $timestamp")
            appendLine("THREAD: ${thread.name}")
            appendLine("DEVICE: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("ANDROID: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("APP: ${context.packageName}")
            appendLine("-".repeat(60))
            appendLine(sw.toString())
            appendLine()
        }

        // Ghi vào app-specific external storage: /Android/data/com.example.app/files/
        val file = File(context.getExternalFilesDir(null), FILE_NAME)
        file.appendText(info)
    }

    /** Đọc toàn bộ crash log (dùng cho UI debug) */
    fun read(context: Context): String {
        val file = File(context.getExternalFilesDir(null), FILE_NAME)
        return if (file.exists()) file.readText() else "(no crash log)"
    }

    /** Xoá crash log */
    fun clear(context: Context) {
        File(context.getExternalFilesDir(null), FILE_NAME).delete()
    }
}