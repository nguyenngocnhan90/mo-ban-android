package com.moban.flow.secondary.create.simple

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.param.NewSecondaryProjectParam
import com.moban.model.response.SimpleResponse
import com.moban.model.response.secondary.constant.ListSecondaryProjectBaseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.SecondaryConstantService
import com.moban.network.service.SecondaryHouseService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by lenvo on 2020-02-25.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class NewSimpleSecondaryPresenterIml : BaseMvpPresenter<INewSimpleSecondaryView>, INewSimpleSecondaryPresenter {
    private val retrofit = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val retrofitLinkHub: Retrofit? = LHApplication.instance.getLinkHubNetComponent()?.retrofit()
    private val secondaryService = retrofitLinkHub?.create(SecondaryHouseService::class.java)
    private val secondaryConstantService = retrofit?.create(SecondaryConstantService::class.java)

    private var view: INewSimpleSecondaryView? = null
    private var context: Context? = null

    override fun attachView(view: INewSimpleSecondaryView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getBaseProjects() {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            return
        }

        secondaryConstantService?.products()?.enqueue(object : Callback<ListSecondaryProjectBaseResponse> {
            override fun onFailure(call: Call<ListSecondaryProjectBaseResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(call: Call<ListSecondaryProjectBaseResponse>?, response: Response<ListSecondaryProjectBaseResponse>?) {
                response?.body()?.data?.let { it ->
                    LHApplication.instance.lhCache.secondaryBaseProjects = it.list
                    LHApplication.instance.lhCache.isLoadedSecondaryConstant = true
                }
            }
        })
    }

    override fun submit(param: NewSecondaryProjectParam) {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            var noNetworkDialog: Dialog? = null
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                submit(param)
            })
            return
        }

        secondaryService?.create(param)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                view?.showCreateSecondaryFailed(null)
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                if (response?.body() == null) {
                    view?.showCreateSecondaryFailed(null)
                    return
                }

                response.body()?.let { it ->
                    if (it.success) {
                        view?.showCreateSecondarySuccess(it.data)
                    } else {
                        view?.showCreateSecondaryFailed(it.error)
                    }
                }
            }

        })
    }
}
