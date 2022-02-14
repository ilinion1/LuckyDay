package com.gerija.vehy

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.facebook.FacebookSdk.fullyInitialize
import com.facebook.FacebookSdk.setAutoInitEnabled
import com.gerija.vehy.databinding.ActivityMainBinding
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var webView: WebView
    private var firstLine = ""
    private var secureGetParametr = ""
    private var secureKey = ""
    private var devTmzKey = ""
    private var adbKey = ""
    private var gadidKey = ""
    private var deeplinkKey = ""
    private var sourceKey = ""
    private var afIdKey = ""
    private var appCampaignKey = ""

    private var devTmz: String? = null
    private lateinit var fullLink: String
    private var gadid: String? = null
    private var deeplink: String? = null
    private var source: String? = null
    private var campaignKey: String? = null
    private var afId: String? = null
    private var adb: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startActionBar()
        startInitialFb()
        getAppsFlyerParams()
        setAppSetting()
    }

    private fun setAppSetting(){
        devTmz = TimeZone.getDefault().id
        adb = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        lifecycleScope.launch(Dispatchers.IO) {
            val googleId= AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
            gadid = googleId.id.toString()
            withContext(Dispatchers.Main){
                startWebView(combineLink())
            }
        }
    }



    private fun setString(){
         firstLine = getString(R.string.firstLine)
         secureGetParametr = getString(R.string.secure_get_parametr)
         secureKey = getString(R.string.secure_key)
         devTmzKey = getString(R.string.dev_tmz_key)
         adbKey = getString(R.string.adb_key)
         gadidKey = getString(R.string.gadid_key)
         deeplinkKey = getString(R.string.deeplink_key)
         sourceKey = getString(R.string.source_key)
         afIdKey = getString(R.string.af_id_key)
         appCampaignKey = getString(R.string.app_campaign_key)
    }

    /**
     * Собираю ссылку
     */
    private fun combineLink(): String{
        setString()
        fullLink = firstLine +
                "?$secureGetParametr=$secureKey" +
                "&$devTmzKey=$devTmz" +
                "&$adbKey=$adb" +
                "&$gadidKey=$gadid" +
                "&$deeplinkKey=$deeplink" +
                "&$sourceKey=$source" +
                "&$appCampaignKey=$source" +
                "&$afIdKey=$afId"

        return fullLink
    }

    /**
     * Инициализирую фейсбук
     */
    private fun startInitialFb(){
        setAutoInitEnabled(true)
        fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(
            this
        ) {
            deeplink = it?.targetUri.toString()
        }
    }


    /**
     * Запускаю прогресс бар
     */
    private fun startActionBar(){
        binding.progressBarId.max = 100
        val currencyProgress = 100

        ObjectAnimator.ofInt(binding.progressBarId,"progress",currencyProgress)
            .setDuration(3000).start()
    }

    /**
     * Получаю параметры с AppsFlyer
     */
    private fun getAppsFlyerParams(){
        afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
        MyApplication.conversionLiveData.observe(this){ data ->
            for (it in data){
                if(it.key == "media_source"){
                    source = it.value.toString()
                }
                if (it.key == "campaign"){
                    campaignKey = it.value.toString()
                }
            }
        }
    }

    /**
     * запускаю webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView(link: String){
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(link)
    }

    private inner class LocalClient: WebViewClient(){
        val localHost = "http://localhost/"

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean{
            return super.shouldOverrideUrlLoading(view, request)
            val url = request?.url.toString()
            if(request?.url.toString() != localHost){

            }
            if (url == localHost){
                startActivity(Intent(this@MainActivity, GameActivity::class.java))
                finish()
            }
            return false
        }

    }
}