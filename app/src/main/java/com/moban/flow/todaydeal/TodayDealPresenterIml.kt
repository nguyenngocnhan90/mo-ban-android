package com.moban.flow.todaydeal

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.chart.ChartResponse
import com.moban.model.response.user.ListDealsResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.DealService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 7/3/18.
 */
class TodayDealPresenterIml: BaseMvpPresenter<ITodayDealView>, ITodayDealPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val dealService = retrofit?.create(DealService::class.java)

    private var view: ITodayDealView? = null
    private var context: Context? = null

    override fun attachView(view: ITodayDealView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadChartData() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        dealService?.chart()?.enqueue(object : Callback<ChartResponse> {
            override fun onFailure(call: Call<ChartResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ChartResponse>?, response: Response<ChartResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        view?.bindingChartData(it)
                    }
                }
            }
        })
    }

    override fun loadTodayDeals(page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        dealService?.today(page)?.enqueue(object : Callback<ListDealsResponse> {
            override fun onFailure(call: Call<ListDealsResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListDealsResponse>?, response: Response<ListDealsResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        var canLoadMore = false
                        it.paging?.let {paging ->
                            canLoadMore = page < paging.total
                        }

                        view?.bindingTodayDeals(it, canLoadMore)
                    }
                }
            }
        })
    }
}
