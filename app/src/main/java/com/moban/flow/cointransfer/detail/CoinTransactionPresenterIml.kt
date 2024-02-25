package com.moban.flow.cointransfer.detail

import android.app.Dialog
import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.userinfo.TransactionsResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by lenvo on 3/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class CoinTransactionPresenterIml : BaseMvpPresenter<ICoinTransactionView>, ICoinTransactionPresenter {
    private var view: ICoinTransactionView? = null
    private var context: Context? = null

    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: ICoinTransactionView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getDetail(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.transaction(id)?.enqueue(object : Callback<TransactionsResponse> {
            override fun onFailure(call: Call<TransactionsResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<TransactionsResponse>?, response: Response<TransactionsResponse>?) {
                response?.body()?.let {
                    it.data?.let { transaction ->
                        view?.bindingTransaction(transaction)
                    }
                }
            }
        })
    }
}
