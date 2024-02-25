package com.moban.flow.deals.reviewdeal

import com.moban.model.data.deal.Deal
import com.moban.model.data.deal.ReviewDeal
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/6/19.
 */
interface IReviewDealView: BaseMvpView {
    fun reviewFailed()
    fun reviewCompleted(reviewDeal: ReviewDeal)
    fun fillDataInformation(deal: Deal)
}
