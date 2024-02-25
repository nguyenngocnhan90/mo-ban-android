package com.moban.flow.todaydeal

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 7/3/18.
 */
interface ITodayDealPresenter: BaseMvpPresenter<ITodayDealView> {
    fun loadChartData()
    fun loadTodayDeals(page: Int)
}
