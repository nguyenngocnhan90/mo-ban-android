package com.moban.flow.deals.reviewdeal

import com.moban.model.param.user.NewDealReviewParam
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/6/19.
 */
interface IReviewDeallPresenter : BaseMvpPresenter<IReviewDealView> {
    fun reviewDeal(param: NewDealReviewParam)
    fun loadDeal(dealId: Int)
}
