package com.example.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.app.core.CrashLogger
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AudioBookApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        CrashLogger.install(this)   // ← THÊM DÒNG NÀY
        PDFBoxResourceLoader.init(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMaxSchedulerLimit(4)
            .build()
}