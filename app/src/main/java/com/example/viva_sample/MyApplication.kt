package com.example.viva_sample

import android.app.Application
import com.facebook.stetho.Stetho
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class MyApplication : Application() {

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        // Debug 모드일때만 Stetho init
        if (BuildConfig.DEBUG) Stetho.initializeWithDefaults(this)
    }

    companion object {
        lateinit var INSTANCE: MyApplication
    }
}