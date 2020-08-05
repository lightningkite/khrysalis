package com.lightningkite.khrysalis.android

import android.app.Application
import androidx.preference.PreferenceManager
import com.lightningkite.khrysalis.ApplicationAccess
import com.lightningkite.khrysalis.Preferences
import com.lightningkite.khrysalis.net.HttpClient

open class KhrysalisApplication: Application() {
    companion object {
        fun setup(application: Application){
            HttpClient.appContext = application
            Preferences.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
            ApplicationAccess.applicationIsActiveStartup(application)
        }
    }

    override fun onCreate() {
        super.onCreate()
        setup(this)
    }
}