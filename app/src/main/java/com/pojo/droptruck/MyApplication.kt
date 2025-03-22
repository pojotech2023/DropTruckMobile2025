package com.pojo.droptruck

import android.app.Application
import com.pojo.droptruck.utils.SharedPreferencesHelper
import dagger.hilt.android.HiltAndroidApp

val prefs: SharedPreferencesHelper by lazy {
    MyApplication.prefs!!
}

@HiltAndroidApp
class MyApplication: Application() {
    companion object {
        var prefs: SharedPreferencesHelper? = null
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        prefs = SharedPreferencesHelper(applicationContext)

    }
}