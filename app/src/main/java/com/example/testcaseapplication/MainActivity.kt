package com.example.testcaseapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.BuildConfig
import com.example.testcaseapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var mainActivityBinding: ActivityMainBinding
    lateinit var webView: WebView
    val remoteConfig = Firebase.remoteConfig
    val file = File(Environment.getExternalStorageDirectory(), "url.txt")
    lateinit var startUrl:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        webView = mainActivityBinding.webview
        setSettingRemoteConfig()
        setSettingWebView()
        openPermissionWindow()
        if(!file.exists()) {
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startUrl = remoteConfig.getString("url")
                    if (startUrl == "" || isEmulator())
                        startActivity(Intent(this, GameActivity::class.java))
                    else {
                        createUrlFile(startUrl)
                        webView.loadUrl(startUrl)
                    }
                }
            }
        }
        else {
            startUrl = file.bufferedReader().readLine()
            if (!hasConnection(this)) {
                 startActivity(Intent(this,NetworkDisconnectActivity::class.java))
            }
            else {
                webView.loadUrl(startUrl)
            }
        }
        setContentView(mainActivityBinding.root)
    }

    override fun onBackPressed() {
        if(startUrl!=webView.url) {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
    }
    private fun createUrlFile(url: String) {
        if (!file.exists() && url.isNotBlank()) {
            val bufferWriter = file.bufferedWriter()
            bufferWriter.write(url)
            file.createNewFile()
            bufferWriter.close()
        }
    }
    private fun setSettingRemoteConfig() {
        val defaults = mapOf(
            "url" to ""
        )
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setDefaultsAsync(defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun setSettingWebView() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        webView.webViewClient = WebViewClient()
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            useWideViewPort = true
            databaseEnabled = true
            setSupportZoom(false)
            allowFileAccess = true
            allowContentAccess = true
        }
    }
    private fun openPermissionWindow() {
        val requestPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    val snackbar: Snackbar =
                        Snackbar.make(
                            mainActivityBinding.root,
                            "Permission is't given",
                            Snackbar.LENGTH_LONG
                        )
                    snackbar.show()
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()
            ) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(
                        java.lang.String.format(
                            "package:%s",
                            getPackageName()
                        )
                    )
                    startActivity(intent, )
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(intent)
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")) || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith(
            "generic" )|| "google_sdk" == Build.PRODUCT
    }
    private fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }
}