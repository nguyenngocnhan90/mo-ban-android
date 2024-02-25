package com.moban.flow.webview

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.moban.LHApplication
import com.moban.R
import com.moban.enum.WebViewOption
import com.moban.flow.splash.SplashActivity
import com.moban.helper.LocalStorage
import com.moban.mvp.BaseMvpActivity
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.nav.view.*


class WebViewActivity : BaseMvpActivity<IWebViewView, IWebViewPresenter>(), IWebViewView {

    override var presenter: IWebViewPresenter = WebViewPresenterIml()
    private var webOption: WebViewOption = WebViewOption.NONE
    private var title: String = ""
    private var link: String = ""
    private var isOneTimeTokenNeeded: Boolean = false

    private var dialog: Dialog? = null

    companion object {
        const val BUNDLE_KEY_OPTION = "BUNDLE_KEY_OPTION"
        const val BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE"
        const val BUNDLE_KEY_LINK = "BUNDLE_KEY_LINK"
        const val BUNDLE_KEY_ONE_TIME_TOKEN = "BUNDLE_KEY_ONE_TIME_TOKEN"

        fun start(context: Context, title: String, link: String, webOption: WebViewOption = WebViewOption.NONE, oneTimeToken: Boolean = false) {
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY_OPTION, webOption.value)
            bundle.putSerializable(BUNDLE_KEY_TITLE, title)
            bundle.putSerializable(BUNDLE_KEY_LINK, link)
            bundle.putSerializable(BUNDLE_KEY_ONE_TIME_TOKEN, oneTimeToken)

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_web_view
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val bundle = intent.extras
        if (intent.hasExtra(BUNDLE_KEY_TITLE)) {
            title = bundle?.getSerializable(BUNDLE_KEY_TITLE) as String

            web_view_nav.nav_tvTitle.text = title
        }

        if (intent.hasExtra(BUNDLE_KEY_OPTION)) {
            webOption = WebViewOption.from(bundle?.getInt(BUNDLE_KEY_OPTION) ?: 0) ?: WebViewOption.NONE
        }

        if (intent.hasExtra(BUNDLE_KEY_LINK)) {
            link = bundle?.getSerializable(BUNDLE_KEY_LINK) as String
        }

        if (intent.hasExtra(BUNDLE_KEY_ONE_TIME_TOKEN)) {
            isOneTimeTokenNeeded = bundle?.getSerializable(BUNDLE_KEY_ONE_TIME_TOKEN) as Boolean
        }

        buildLink()

        web_view_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        webview_btn_confirm.visibility = if (webOption == WebViewOption.NONE) View.GONE else View.VISIBLE

        webview_btn_confirm.setOnClickListener {
            dialog = DialogUtil.showConfirmDialog(this, true,
                R.string.delete_account, R.string.delete_account_question,
                R.string.confirm, R.string.skip,
                View.OnClickListener {
                    dialog?.dismiss()
                    deleteAccount()
                }, null)
        }
    }

    private fun deleteAccount() {
        if (!NetworkUtil.hasConnection(this)) {
            dialog = DialogUtil.showNetworkError(this, View.OnClickListener {
                dialog?.dismiss()
                deleteAccount()
            })
            return
        }

        webview_progressbar.visibility = View.VISIBLE
        presenter.deleteAccount()
    }

    private fun buildLink() {
        web_view_container.webViewClient = object: WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                webview_progressbar.visibility = View.VISIBLE
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webview_progressbar.visibility = View.GONE
            }
        }
        web_view_container.webChromeClient = WebChromeClient()
        web_view_container.settings.useWideViewPort = true
        web_view_container.settings.loadWithOverviewMode = true
        web_view_container.settings.domStorageEnabled = true
        web_view_container.settings.javaScriptEnabled = true
        web_view_container.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        web_view_container.settings.useWideViewPort = true
        web_view_container.settings.loadWithOverviewMode = true
        web_view_container.settings.domStorageEnabled = true
        web_view_container.settings.builtInZoomControls = true
        web_view_container.settings.displayZoomControls = false

        val cookieManager = CookieManager.getInstance()
        cookieManager.removeSessionCookies {  }
        var uri = Uri.parse(link)
        if (isOneTimeTokenNeeded) {
            uri = uri.buildUpon().appendQueryParameter("otp", LocalStorage.token()).build()
        }
        Log.i("TAG", "Load URL $uri")
        web_view_container.loadUrl(uri.toString())
    }

    private fun deleteUserInfo() {
        LocalStorage.saveToken(null)
        LocalStorage.saveUser(null)
        LocalStorage.saveUnReadNotification(0)
        LHApplication.instance.lhCache.popup = null
        LHApplication.instance.lhCache.oneTimeToken = ""
    }

    private fun backToLogin() {
        SplashActivity.start(this)
    }

    override fun loadUrl(token: String) {
        webview_progressbar.visibility = View.GONE
        LHApplication.instance.lhCache.oneTimeToken = token
        buildLink()
    }

    override fun deleteAccount(success: Boolean) {
        val message = if (success) "Bạn đã xóa tài khoản thành công!" else "Xóa tài khoản không thành công! Vui lòng thử lại sau!"
        webview_progressbar.visibility = View.GONE

        dialog = DialogUtil.showInfoDialog(this, "Thông báo", message, getString(R.string.ok), View.OnClickListener {
            dialog?.dismiss()

            if (success) {
                deleteUserInfo()
                backToLogin()
            }
        })
    }
}
