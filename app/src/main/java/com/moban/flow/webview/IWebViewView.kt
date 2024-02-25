package com.moban.flow.webview

import com.moban.mvp.BaseMvpView

interface IWebViewView: BaseMvpView {
    fun loadUrl(token: String)
    fun deleteAccount(success: Boolean)
}
