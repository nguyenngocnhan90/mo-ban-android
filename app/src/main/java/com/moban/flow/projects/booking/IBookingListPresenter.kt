package com.moban.flow.projects.booking

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by H. Len Vo on 10/6/18.
 */
interface IBookingListPresenter: BaseMvpPresenter<IBookingListView> {
    fun loadBookings(projectId: Int, page: Int)
}
