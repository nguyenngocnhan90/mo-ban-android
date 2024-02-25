package com.moban.flow.projectdetail.bookingdetail

import android.graphics.Bitmap
import com.moban.model.data.booking.ProjectBooking
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 3/22/18.
 */
interface IBookingDetailPresenter: BaseMvpPresenter<IBookingDetailView> {
    fun loadBookingDetail(id: Int)
    fun uploadInvoice(projectBooking: ProjectBooking, bitmap: Bitmap, type: Int, retry: Int = 1)
}
