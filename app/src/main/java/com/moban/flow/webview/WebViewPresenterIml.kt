package com.moban.flow.webview

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.SimpleResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebViewPresenterIml: BaseMvpPresenter<IWebViewView>, IWebViewPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)
    private var view: IWebViewView? = null
    private var context: Context? = null

    override fun attachView(view: IWebViewView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getOneTimeToken() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.oneTimeToken()?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                response?.body()?.data?.let {
                    view?.loadUrl(it)
                }
            }
        })
    }

    override fun deleteAccount() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.deleteAccount()?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                t.toString()
                view?.deleteAccount(false)
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                response?.body()?.let {
                    view?.deleteAccount(it.success)
                }
            }
        })
    }
}
