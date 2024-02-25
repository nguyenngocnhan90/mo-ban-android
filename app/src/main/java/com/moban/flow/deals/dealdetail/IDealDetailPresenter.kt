package com.moban.flow.deals.dealdetail

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/6/18.
 */
interface IDealDetailPresenter : BaseMvpPresenter<IDealDetailView> {
    fun loadDeal(id: Int)
}
