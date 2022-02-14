package com.gerija.vehy

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal

class MyApplication: Application() {

    private val keyDevAppsflyer = "s2uD2SPSCbdWE7ERtTu9y3"
    private val oneSignalAppId = "96d86473-1bc4-4147-b6eb-096a3171d294"

    companion object{
        var conversionLiveData = MutableLiveData<MutableMap<String, Any>>()
    }


    override fun onCreate() {
        super.onCreate()
        AppsFlyerLib.getInstance().init(keyDevAppsflyer, appsFlyerConversion(), this)
        AppsFlyerLib.getInstance().start(this)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)
    }

    /**
     * Обрабатываю данные о конверсиях
     */
    private fun appsFlyerConversion(): AppsFlyerConversionListener{
        return object : AppsFlyerConversionListener{
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                Log.d("onConversionDataSuccess", data.toString())
                conversionLiveData.postValue(data)
            }

            override fun onConversionDataFail(error: String?) {
                Log.d("onConversionDataFail", "$error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {}

            override fun onAttributionFailure(error: String?) {
                Log.d("onAttributionFailure", "$error")
            }

        }
    }
}