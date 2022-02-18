package com.gerija.vehy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.LOAD_DEFAULT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk.fullyInitialize
import com.facebook.FacebookSdk.setAutoInitEnabled
import com.facebook.applinks.AppLinkData
import com.gerija.vehy.databinding.ActivityMainBinding
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val keyDevAppsflyer = "s2uD2SPSCbdWE7ERtTu9y3"

    //private var webView: WebView? = null
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
        AppsFlyerLib.getInstance().init(keyDevAppsflyer, appsFlyerConversion(), this)
        AppsFlyerLib.getInstance().start(this)
        startInitialFb()

    }

    private fun setAppSetting() {
        devTmz = TimeZone.getDefault().id
        adb = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
        lifecycleScope.launch(Dispatchers.IO) {
            val googleId = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
            gadid = googleId.id.toString()
            withContext(Dispatchers.Main) {
                setString()
                startWebView()
            }
        }
    }


    /**
     * Обрабатываю данные о конверсиях
     */
    private fun appsFlyerConversion(): AppsFlyerConversionListener {
        afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)

        return object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {

                data?.let {
                    getAppsFlyerParams(it)

                }
            }

            override fun onConversionDataFail(error: String?) {
                source = null
                campaignKey = null
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                Log.d("onAttributionFailure", "$data")
            }

            override fun onAttributionFailure(error: String?) {
                Log.d("onAttributionFailure", "$error")
                //   conversionLiveData.postValue(mutableMapOf())
            }

        }
    }


    /**
     * Собираю ссылку
     */
    private fun setString() {
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

        fullLink = firstLine +
                "?$secureGetParametr=$secureKey" +
                "&$devTmzKey=$devTmz" +
                "&$adbKey=$adb" +
                "&$gadidKey=$gadid" +
                "&$deeplinkKey=$deeplink" +
                "&$sourceKey=$source" +
                "&$appCampaignKey=$campaignKey" +
                "&$afIdKey=$afId"

    }

    /**
     * Инициализирую фейсбук
     */
    private fun startInitialFb() {
        setAutoInitEnabled(true)
        fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(
            this
        ) {
            deeplink = it?.targetUri.toString()
        }
    }


    /**
     * Получаю параметры с AppsFlyer
     */
    private fun getAppsFlyerParams(data: MutableMap<String, Any>) {
        for (inform in data) {
            if (inform.key == "media_source") {
                source = inform.value.toString()
                Log.d("Log", "$source")
            }
            if (inform.key == "campaign") {
                campaignKey = inform.value.toString()
                Log.d("Log", "$campaignKey")
            }
        }
        setAppSetting()
    }


    /**
     * запускаю webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView() {
        binding.webViewId.loadUrl(fullLink)
        binding.webViewId.settings.javaScriptEnabled = true
        binding.webViewId.settings.domStorageEnabled = true
        binding.webViewId.settings.loadWithOverviewMode = true

        binding.webViewId.clearCache(false)
        binding.webViewId.settings.cacheMode = LOAD_DEFAULT

        binding.webViewId.webChromeClient = ChromeClient()

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webViewId, true)

        val linkBot = getSharedPreferences("link", Context.MODE_PRIVATE)
        val localBot = linkBot.getBoolean("local", false)

        val user = getSharedPreferences("hasVisited", Context.MODE_PRIVATE)
        val visited = user.getBoolean("hasVisited", false)

        if (!visited) {
            user.edit().putBoolean("hasVisited", true).apply()
            binding.webViewId.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    if (url == "http://localhost/") {
                        linkBot.edit().putBoolean("local", true).apply()
                        startActivity(Intent(this@MainActivity, GameActivity::class.java))
                    } else {
                        binding.webViewId.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            if (localBot) {
                startActivity(Intent(this@MainActivity, GameActivity::class.java))
            } else {
                binding.webViewId.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        binding.webViewId.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private inner class ChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri?>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {

            return true
        }


    }

    override fun onBackPressed() {
        if (binding.webViewId.canGoBack()) {
            binding.webViewId.goBack()
        } else super.onBackPressed()
    }

}
