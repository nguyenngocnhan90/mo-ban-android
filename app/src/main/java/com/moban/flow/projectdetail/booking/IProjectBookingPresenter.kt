package com.moban.flow.projectdetail.booking

import com.moban.model.data.booking.BookingParam
import com.moban.model.data.project.Apartment
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 3/19/18.
 */
interface IProjectBookingPresenter: BaseMvpPresenter<IProjectBookingView> {
    fun loadApartment(apartment: Apartment)
    fun bookingApartment(booking: BookingParam)
}
