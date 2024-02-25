package com.moban.flow.cointransfer.history

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.helper.LocalStorage
import com.moban.model.response.coin.ListLinkPointTransactionsResponse
import com.moban.model.response.coin.ListTransactionsResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CoinHistoryPresenterIml: BaseMvpPresenter<ICoinHistoryView>, ICoinHistoryPresenter {
    private var view: ICoinHistoryView? = null
    private var context: Context? = null

    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: ICoinHistoryView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadTransactions(page: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                loadTransactions(page)
            })
            return
        }

        LocalStorage.user()?.let {user ->
            userService?.transactions(user.id, page)?.enqueue(object : Callback<ListTransactionsResponse> {
                override fun onFailure(call: Call<ListTransactionsResponse>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<ListTransactionsResponse>?, response: Response<ListTransactionsResponse>?) {
                    response?.body()?.let { it ->
                        if (it.success) {
                            it.data?.let {
                                view?.bindingTransactions(it)
                            }
                        }
                    }
                }
            })
        }
    }

    override fun loadLinkPointTransactions(page: Int) {
        val context = context!!

        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                loadLinkPointTransactions(page)
            })
            return
        }

        LocalStorage.user()?.let {user ->
            userService?.linkpoints(user.id, page)?.enqueue(object : Callback<ListLinkPointTransactionsResponse> {
                override fun onFailure(call: Call<ListLinkPointTransactionsResponse>?, t: Throwable?) {
                    t.toString()
                }

                override fun onResponse(call: Call<ListLinkPointTransactionsResponse>?, response: Response<ListLinkPointTransactionsResponse>?) {
                    response?.body()?.let { it ->
                        if (it.success) {
                            it.data?.let {
                                view?.bindingLinkPointTransactions(it)
                            }
                        }
                    }
                }
            })
        }
    }
}
