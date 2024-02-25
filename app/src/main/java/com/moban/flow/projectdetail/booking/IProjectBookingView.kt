package com.moban.flow.projectdetail.booking

import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Apartment
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 3/19/18.
 */
interface IProjectBookingView: BaseMvpView {
    fun loadApartment(apartment: Apartment)
    fun showBookingApartmentSuccess(projectBooking: ProjectBooking, message: String? = null)
    fun showBookingApartmentFailed(message: String? = null)
}
