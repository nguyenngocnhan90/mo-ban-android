package com.moban.flow.projectdetail.bookingdetail

import android.graphics.Bitmap
import com.moban.model.data.booking.ProjectBooking
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 3/22/18.
 */
interface IBookingDetailView: BaseMvpView {
    fun loadApartmentDetail(projectBooking: ProjectBooking)
    fun showDialogUpdateInvoiceFailed(type: Int)
    fun showDialogUpdateInvoiceSuccess(bitmap: Bitmap, type: Int, message: String = "")
}
