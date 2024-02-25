package com.moban.flow.projects.booking.newbooking

import com.moban.model.data.booking.ProjectBooking
import com.moban.mvp.BaseMvpView

interface INewBookingView : BaseMvpView {
    fun bookingCompleted(book: ProjectBooking, message: String = "")
    fun bookingFailed(error: String? = null)
}
