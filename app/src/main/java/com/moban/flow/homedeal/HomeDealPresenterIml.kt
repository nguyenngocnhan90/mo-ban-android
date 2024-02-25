package com.moban.flow.homedeal

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.document.Document
import com.moban.model.data.homedeal.HomeDealDetail
import com.moban.model.response.BaseListResponse
import com.moban.model.response.BaseResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.HomeService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


/**
 * Created by H. Len Vo on 6/13/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class HomeDealPresenterIml: BaseMvpPresenter<IHomeDealView>, IHomeDealPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val homeService = retrofit?.create(HomeService::class.java)

    private var view: IHomeDealView? = null
    private var context: Context? = null

    override fun attachView(view: IHomeDealView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun get(typeId: String) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        homeService?.dealDetail(typeId)?.enqueue(object : Callback<BaseResponse<HomeDealDetail>> {
            override fun onFailure(call: Call<BaseResponse<HomeDealDetail>>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<BaseResponse<HomeDealDetail>>?, response: Response<BaseResponse<HomeDealDetail>>?) {
                response?.body()?.let {
                    it.data?.let { homeDeal ->
                        view?.bindingDealDetail(homeDeal)
                    }
                }
            }
        })
    }

    override fun getListDeals(typeId: String, page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        homeService?.getListDeals(typeId, page)?.enqueue(object : Callback<BaseListResponse<Document>> {
            override fun onFailure(call: Call<BaseListResponse<Document>>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<BaseListResponse<Document>>?, response: Response<BaseListResponse<Document>>?) {
                response?.body()?.let {
                    it.data?.let { deals ->
                        val canLoadMore = deals.paging?.localCanLoadMore() ?: false
                        view?.bindingDealsList(deals.list, canLoadMore)
                    }
                }
            }
        })
    }
}
