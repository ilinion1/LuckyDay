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
    private val keyDevAppsflyer = "s2uD2SPSCbdWE7ERtTu9y3"

    companion object {
        var liveDataAppsFlyer = MutableLiveData<MutableMap<String, Any>>()
    }

    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)

        AppsFlyerLib.getInstance().init(keyDevAppsflyer, appsFlyerConversion(), this)
        AppsFlyerLib.getInstance().start(this)
    }

    /**
     * Обрабатываю данные о конверсиях
     */
    private fun appsFlyerConversion(): AppsFlyerConversionListener {

        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {

                data?.let {
                    liveDataAppsFlyer.postValue(it)
                    Log.d("MyLog","успех")
                }
            }

            override fun onConversionDataFail(error: String?) {
                liveDataAppsFlyer.postValue(mutableMapOf())
                Log.d("MyLog","не1")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                liveDataAppsFlyer.postValue(mutableMapOf())
                Log.d("MyLog","не2")
            }

            override fun onAttributionFailure(error: String?) {
                liveDataAppsFlyer.postValue(mutableMapOf())
                Log.d("MyLog","не3")
            }

        }
    }

}