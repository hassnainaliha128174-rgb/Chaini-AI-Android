package com.chaini.ai

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : Activity() {
    private var rewardedAd: RewardedAd? = null
    private lateinit var web: WebView
    private lateinit var topBanner: AdView
    private lateinit var bottomBanner: AdView
    private val webUrl = "https://chaini-ai-app-free-test-safe-v11.vercel.app"
    private val rewardedUnit = "ca-app-pub-3940256099942544/5224354917"
    private val bannerUnit = "ca-app-pub-3940256099942544/6300978111"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        topBanner = makeBanner()
        root.addView(topBanner, LinearLayout.LayoutParams(-1, -2))

        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            addJavascriptInterface(AdsBridge(), "AndroidAds")
            webViewClient = WebViewClient()
        }
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))

        bottomBanner = makeBanner()
        root.addView(bottomBanner, LinearLayout.LayoutParams(-1, -2))
        setContentView(root)

        if (webUrl.startsWith("http")) web.loadUrl(webUrl)
        else Toast.makeText(this, "Vercel URL ابھی set نہیں کیا گیا", Toast.LENGTH_LONG).show()
        loadRewardedAd()
        setHomeBanners(false)
    }

    private fun makeBanner(): AdView = AdView(this).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = bannerUnit
        loadAd(AdRequest.Builder().build())
    }

    private fun setHomeBanners(home: Boolean) {
        val v = if (home) View.VISIBLE else View.GONE
        topBanner.visibility = v
        bottomBanner.visibility = v
    }

    private fun loadRewardedAd() {
        RewardedAd.load(this, rewardedUnit, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
            override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) { rewardedAd = null }
        })
    }

    private fun showRewarded(requestId: String) {
        val ad = rewardedAd
        if (ad == null) {
            web.post { web.evaluateJavascript("window.onChainiRewardedAdFailed(" + jsString(requestId) + "," + jsString("Rewarded ad ابھی load ہو رہا ہے۔ دوبارہ کوشش کریں۔") + ")", null) }
            loadRewardedAd()
            return
        }
        rewardedAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { loadRewardedAd() }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                web.post { web.evaluateJavascript("window.onChainiRewardedAdFailed(" + jsString(requestId) + "," + jsString(adError.message ?: "Rewarded ad failed.") + ")", null) }
                loadRewardedAd()
            }
        }
        ad.show(this) {
            web.post { web.evaluateJavascript("window.onChainiRewardedAdComplete(" + jsString(requestId) + ")", null) }
        }
    }

    private fun jsString(value: String): String = "'" + value
        .replace("\\", "\\\\")
        .replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\r", "\\r") + "'"

    inner class AdsBridge {
        @JavascriptInterface
        fun showRewardedAd(requestId: String, reason: String?) = runOnUiThread { showRewarded(requestId) }

        @JavascriptInterface
        fun onScreenChanged(screen: String?) = runOnUiThread { setHomeBanners(screen == "home") }
    }
}
