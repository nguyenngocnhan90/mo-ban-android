package com.moban.flow.projects.booking

import com.moban.model.response.project.ListProjectBooking
import com.moban.mvp.BaseMvpView

/**
 * Created by H. Len Vo on 10/6/18.
 */
interface IBookingListView: BaseMvpView {
    fun bindingBookings(bookings: ListProjectBooking, canLoadMore: Boolean)
}
