package com.moban.flow.reservation.list

import com.moban.enum.CombinedDealStatus
import com.moban.enum.DealFilter
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpPresenter

interface IListReservationPresenter: BaseMvpPresenter<IListReservationView> {
    fun loadProjectDeals(projectId: Int, combinedDealStatus: CombinedDealStatus?)
    fun loadAllDeals(user: User, page: Int, combinedDealStatus: CombinedDealStatus?, filter: DealFilter)
    fun cancel(id: Int)
}
