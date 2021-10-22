package com.soapclient.place

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.kakao.sdk.common.KakaoSdk
import com.soapclient.place.data.BuildConfig.KAKAO_SDK_KEY
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {
    lateinit var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        Thread.getDefaultUncaughtExceptionHandler()?.let {
            uncaughtExceptionHandler = it
            Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerApplication())
        }

        super.onCreate()
        KakaoSdk.init(this, KAKAO_SDK_KEY)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    inner class UncaughtExceptionHandlerApplication: Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            uncaughtExceptionHandler.uncaughtException(t, e)
        }
    }
}