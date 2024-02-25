package com.moban.flow.projects.booking.newbooking

import com.moban.model.data.booking.BookingParam
import com.moban.mvp.BaseMvpPresenter

interface INewBookingPresenter: BaseMvpPresenter<INewBookingView> {
    fun book(param: BookingParam)
}
