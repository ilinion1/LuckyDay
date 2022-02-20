package com.gerija.vehy

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.LOAD_DEFAULT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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


    private var fileData: ValueCallback<Uri>? = null
    private var filePath: ValueCallback<Array<Uri>>? = null

    companion object {
        private const val FILE_CHOOSER_REQUEST_CODE = 10000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startInitialFb()
        getAppsFlyerParams()

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
    private fun getAppsFlyerParams() {
        afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
            MyApplication.liveDataAppsFlyer.observe(this){
                for (inform in it) {
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
    }


    /**
     * запускаю webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun startWebView() = with(binding) {
        val saveLink = getSharedPreferences("saveLink", Context.MODE_PRIVATE)
        val linkSaveState = saveLink.getString("saveLink", "")
        if (linkSaveState.isNullOrEmpty()){
            webViewId.loadUrl(fullLink)
        } else{
            webViewId.loadUrl(linkSaveState)
        }

        webViewId.settings.javaScriptEnabled = true
        webViewId.settings.domStorageEnabled = true
        webViewId.settings.loadWithOverviewMode = true

        webViewId.clearCache(false)
        webViewId.settings.cacheMode = LOAD_DEFAULT

        webViewId.webChromeClient = ChromeClient()

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webViewId, true)

        val linkBot = getSharedPreferences("link", Context.MODE_PRIVATE)
        val localBot = linkBot.getBoolean("local", false)

        val user = getSharedPreferences("hasVisited", Context.MODE_PRIVATE)
        val visited = user.getBoolean("hasVisited", false)


        if (!visited) {
            user.edit().putBoolean("hasVisited", true).apply()
            webViewId.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    saveLink.edit().putString("saveLink", url).apply()
                    if (url == "http://localhost/") {

                        linkBot.edit().putBoolean("local", true).apply()
                        startActivity(Intent(this@MainActivity, GameActivity::class.java))
                    } else {

                        webViewId.visibility = View.VISIBLE
                    }
                }
            }
        } else {

                if (localBot) {

                    startActivity(Intent(this@MainActivity, GameActivity::class.java))
                } else {

                    webViewId.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            webViewId.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }


    private inner class ChromeClient : WebChromeClient() {

        // For Android >= 5.0
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            filePath = filePathCallback
            openImageChooserActivity()
            return true
        }
    }

    private fun openImageChooserActivity() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startActivityForResult(i, FILE_CHOOSER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (null == fileData && null == filePath) return
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (filePath != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (fileData != null) {
                fileData!!.onReceiveValue(result)
                fileData = null
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode != FILE_CHOOSER_REQUEST_CODE || filePath == null)
            return
        var results: Array<Uri>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = Array(clipData.itemCount){
                            i -> clipData.getItemAt(i).uri
                    }
                }
                if (dataString != null)
                    results = arrayOf(Uri.parse(dataString))
            }
        }
        filePath!!.onReceiveValue(results)
        filePath = null
    }


override fun onBackPressed() {
    if (binding.webViewId.canGoBack()) {
        binding.webViewId.goBack()
    } else super.onBackPressed()
}

}
