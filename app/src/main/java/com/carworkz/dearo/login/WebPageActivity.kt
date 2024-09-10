package com.carworkz.dearo.login

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.utils.Constants.ApiConstants
import javax.inject.Inject

class WebPageActivity : BaseActivity() {

    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var progressView: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        (application as DearOApplication).repositoryComponent.COMPONENT(this)
        val type = intent.extras?.getString(TYPE, TERMS_AND_CONDITION)
        screenTracker.sendScreenEvent(this, when (type) {
            TERMS_AND_CONDITION -> ScreenTracker.SCREEN_TERMS_CONDITION
            else -> ScreenTracker.SCREEN_PRIVACY_POLICY
        }, this.javaClass.name)
        val webView = find<WebView>(R.id.viewer)
        progressView = find(R.id.progressBar)
        val toolbar = find<Toolbar>(R.id.toolbar_action)
        setSupportActionBar(toolbar)
        val actionBar = this.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            toolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_clear_white_24dp)
            when (type) {
                TERMS_AND_CONDITION -> {
                    actionBar.title = "Terms and Condition"
                }

                PRIVACY_POLICY -> {
                    actionBar.title = "Privacy Policy"
                }

                SPARES -> {
                    actionBar.title = "Spares"
                }
            }
        }
        webView.run {
            settings.javaScriptEnabled = true
            settings.allowFileAccessFromFileURLs = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.domStorageEnabled = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    dismissProgressBar()
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    super.onReceivedSslError(view, handler, error)
                    handler?.proceed()
                }
            }
        }

        if (checkIfNetworkAvailable()) {
            showProgressBar()
            when (type) {
                TERMS_AND_CONDITION -> {
                    webView.loadUrl(ApiConstants.URL_TERMS_AND_CONDITION)
                }

                PRIVACY_POLICY -> {
                    webView.loadUrl(ApiConstants.URL_PRIVACY_POLICY)
                }

                SPARES -> {
                    webView.loadUrl("http://52.187.120.183:5000/")
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun getProgressView(): View {
        return progressView
    }

    companion object {
        const val TYPE = "type"
        const val TERMS_AND_CONDITION = "t&c"
        const val PRIVACY_POLICY = "pp"
        const val SPARES = "spares"
    }
}
