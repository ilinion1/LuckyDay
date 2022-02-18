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
    companion object{
        var conversionLiveData = MutableLiveData<MutableMap<String, Any>>()
    }

    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)

        AppsFlyerLib.getInstance().init(keyDevAppsflyer, appsFlyerConversion(), this)
        AppsFlyerLib.getInstance().start(this)
    }

    private fun appsFlyerConversion(): AppsFlyerConversionListener {

        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {

                data?.let {
                    conversionLiveData.postValue(it)
                }
            }

            override fun onConversionDataFail(error: String?) {
                conversionLiveData.postValue(mutableMapOf())
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                Log.d("onAttributionFailure", "$data")
                conversionLiveData.postValue(mutableMapOf())
            }

            override fun onAttributionFailure(error: String?) {
                Log.d("onAttributionFailure", "$error")
                conversionLiveData.postValue(mutableMapOf())

            }

        }
    }
}