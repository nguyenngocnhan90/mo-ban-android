package com.moban.flow.deals.dealdetail

import com.moban.model.data.deal.Deal
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/6/18.
 */
interface IDealDetailView: BaseMvpView {
    fun bindingDeal(deal: Deal)
}
