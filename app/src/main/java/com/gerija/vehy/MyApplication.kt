package com.gerija.vehy

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal
import kotlinx.coroutines.delay

class MyApplication: Application() {

    private val oneSignalAppId = "96d86473-1bc4-4147-b6eb-096a3171d294"

    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)
    }

}