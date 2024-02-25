package com.moban.flow.todaydeal

import com.moban.model.data.chart.ChartData
import com.moban.model.response.deal.ListDeals
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 7/3/18.
 */
interface ITodayDealView: BaseMvpView {
    fun bindingChartData(data: List<ChartData>)
    fun bindingTodayDeals(listDeals: ListDeals, canLoadMore: Boolean)
}
