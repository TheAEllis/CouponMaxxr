package com.github.theaellis.couponmaxxr

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    // State machine to track if we are waiting for the user to log in
    private var pendingCouponsUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        val btnPublix = findViewById<LinearLayout>(R.id.btnPublix)
        val btnFoodLion = findViewById<LinearLayout>(R.id.btnFoodLion)

        setupWebView()

        // --- MODERN BACK BUTTON HANDLER ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // --- STORE LAUNCHERS (Using the Registry) ---
        btnPublix.setOnClickListener {
            val publix = RetailerRegistry.stores.find { it.name == "Publix" }
            publix?.let { launchStoreFlow(it) }
        }

        btnFoodLion.setOnClickListener {
            val foodLion = RetailerRegistry.stores.find { it.name == "Food Lion" }
            foodLion?.let { launchStoreFlow(it) }
        }
    }

    /**
     * Determines the flow: Straight to coupons if logged in, or login page first.
     */
    private fun launchStoreFlow(retailer: Retailer) {
        val cookies = CookieManager.getInstance().getCookie(retailer.domain)

        if (retailer.hasValidSession(cookies)) {
            pendingCouponsUrl = null
            webView.loadUrl(retailer.couponsUrl)
        } else {
            pendingCouponsUrl = retailer.couponsUrl
            Toast.makeText(this, "Session missing. Please log in.", Toast.LENGTH_LONG).show()
            webView.loadUrl(retailer.loginUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        settings.userAgentString = desktopUserAgent
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                // Find which retailer matches the current URL
                val currentRetailer = RetailerRegistry.stores.find { url.contains(it.domain) } ?: return

                // 1. Are we currently waiting for the user to log in?
                if (pendingCouponsUrl != null) {
                    val cookies = CookieManager.getInstance().getCookie(url)

                    if (currentRetailer.hasValidSession(cookies)) {
                        Toast.makeText(this@MainActivity, "Login detected! Loading coupons...", Toast.LENGTH_SHORT).show()
                        val target = pendingCouponsUrl!!
                        pendingCouponsUrl = null
                        view.loadUrl(target)
                    } else if (currentRetailer.name == "Food Lion" && (url == "https://foodlion.com/" || url == "https://www.foodlion.com/")) {
                        // Pop the modal helper specifically for Food Lion
                        view.evaluateJavascript(getFoodLionLoginHelperScript(), null)
                    }
                    return
                }

                // 2. We are authenticated, read the script from assets and inject it
                if (url.contains(currentRetailer.couponsUrl)) {
                    try {
                        val script = applicationContext.assets.open(currentRetailer.scriptFilename).bufferedReader().use { it.readText() }
                        view.evaluateJavascript(script, null)
                        progressBar.visibility = ProgressBar.VISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@MainActivity, "Error loading clipper script", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    progressBar.visibility = ProgressBar.GONE
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    // --- JAVASCRIPT INTERFACE ---
    inner class AndroidBridge {
        @JavascriptInterface
        fun onClippingComplete(storeName: String) {
            runOnUiThread {
                progressBar.visibility = ProgressBar.GONE
                Toast.makeText(this@MainActivity, "$storeName clipping complete!", Toast.LENGTH_LONG).show()
            }
        }

        @JavascriptInterface
        fun onSessionInvalid(loginUrl: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Session expired. Redirecting to login...", Toast.LENGTH_SHORT).show()
                pendingCouponsUrl = webView.url
                webView.loadUrl(loginUrl)
            }
        }
    }

    // --- HELPER SCRIPTS ---
    private fun getFoodLionLoginHelperScript(): String {
        return """
            (function() {
                console.log("Login Helper injected. Waiting for header to render...");
                
                setTimeout(() => {
                    const accountBtn = document.getElementById('header-account-button') || 
                                       Array.from(document.querySelectorAll('button')).find(b => 
                                           b.textContent.toLowerCase().includes('sign in') || 
                                           b.textContent.toLowerCase().includes('log in')
                                       );
                    
                    if (accountBtn) {
                        console.log("Account button found. Simulating click to open modal...");
                        ['mouseenter', 'mousedown', 'mouseup', 'click'].forEach(eventType => {
                            const event = new MouseEvent(eventType, { bubbles: true, cancelable: true, view: window, buttons: 1 });
                            accountBtn.dispatchEvent(event);
                        });
                    } else {
                        console.log("Could not locate the Sign In button.");
                    }
                }, 1500); 
            })();
        """.trimIndent()
    }
}