package com.moban.flow.reservation.list

import com.moban.model.data.deal.Deal
import com.moban.mvp.BaseMvpView

interface IListReservationView: BaseMvpView {
    fun bindingDeals(deals: List<Deal>, canLoadMore: Boolean)

    fun cancelReservationFailed()
    fun cancelReservationCompleted()
}
