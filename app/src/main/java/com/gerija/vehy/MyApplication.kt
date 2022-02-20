package com.gerija.vehy

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal

class MyApplication: Application() {

    private val oneSignalAppId = "96d86473-1bc4-4147-b6eb-096a3171d294"
    private val keyDevAppsflyer = "s2uD2SPSCbdWE7ERtTu9y3"
    companion object{
        var liveDataAppsFlyer = MutableLiveData<MutableMap<String, Any>>()
    }
    private var dataSeted = false

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

                if (!dataSeted){
                    data?.let {
                        liveDataAppsFlyer.postValue(it)
                    }
                    dataSeted = true
                }

            }

            override fun onConversionDataFail(error: String?) {
                if (!dataSeted){
                    liveDataAppsFlyer.postValue(mutableMapOf())
                    dataSeted = true
                }
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                if (!dataSeted){
                    liveDataAppsFlyer.postValue(mutableMapOf())
                    dataSeted = true
                }
            }

            override fun onAttributionFailure(error: String?) {
                if (!dataSeted){
                    liveDataAppsFlyer.postValue(mutableMapOf())
                    dataSeted = true
                }
            }
        }
    }
}