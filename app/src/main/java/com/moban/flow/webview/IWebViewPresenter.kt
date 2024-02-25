package com.moban.flow.webview

import com.moban.mvp.BaseMvpPresenter

interface IWebViewPresenter: BaseMvpPresenter<IWebViewView> {
    fun getOneTimeToken()
    fun deleteAccount()
}
