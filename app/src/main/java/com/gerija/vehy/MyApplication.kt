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

    private val keyDevAppsflyer = "s2uD2SPSCbdWE7ERtTu9y3"
    private val oneSignalAppId = "96d86473-1bc4-4147-b6eb-096a3171d294"

    companion object{
        var conversionLiveData = MutableLiveData<MutableMap<String, Any>>()
    }

    private var dataSeted = false


    override fun onCreate() {
        super.onCreate()
//        AppsFlyerLib.getInstance().init(keyDevAppsflyer, appsFlyerConversion(), this)
//        AppsFlyerLib.getInstance().start(this)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSignalAppId)
    }

//    /**
//     * Обрабатываю данные о конверсиях
//     */
//    private fun appsFlyerConversion(): AppsFlyerConversionListener{
//
//        return object : AppsFlyerConversionListener{
//            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
//                Log.d("onConversion", data.toString())
//                data?.let {
//                    if (!dataSeted) {
//                        dataSeted = true
//                        conversionLiveData.postValue(it)
//                    }
//                }
//                Log.d("onConversionDataSuccess", data.toString())
//               // conversionLiveData.postValue("test")
//            }
//
//            override fun onConversionDataFail(error: String?) {
//                if (!dataSeted) {
//                    dataSeted = true
//                    conversionLiveData.postValue(mutableMapOf())
//                }
//            }
//
//            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
//             //   conversionLiveData.postValue(mutableMapOf())
//            }
//
//            override fun onAttributionFailure(error: String?) {
//                Log.d("onAttributionFailure", "$error")
//             //   conversionLiveData.postValue(mutableMapOf())
//            }
//
//        }
//    }
}