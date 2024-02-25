package com.moban.flow.projectdetail.bookingdetail

import android.content.Context
import android.graphics.Bitmap
import com.moban.LHApplication
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.param.UpdateInvoiceParam
import com.moban.model.response.PhotoResponse
import com.moban.model.response.project.ProjectBookingResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.BookingService
import com.moban.network.service.ImageService
import com.moban.util.BitmapUtil
import com.moban.util.DateUtil
import com.moban.util.NetworkUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by LenVo on 3/22/18.
 */
class BookingDetailPresenterIml : BaseMvpPresenter<IBookingDetailView>, IBookingDetailPresenter {

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val bookingService = retrofit?.create(BookingService::class.java)
    private val imageService = retrofit?.create(ImageService::class.java)

    private var view: IBookingDetailView? = null
    private var context: Context? = null

    override fun attachView(view: IBookingDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadBookingDetail(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        bookingService?.booking(id)?.enqueue(object : Callback<ProjectBookingResponse> {
            override fun onFailure(call: Call<ProjectBookingResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ProjectBookingResponse>?, response: Response<ProjectBookingResponse>?) {
                response?.body()?.let { it ->
                    if (it.success) {
                        it.data?.let {
                            view?.loadApartmentDetail(it)
                        }
                    }
                }
            }
        })
    }

    override fun uploadInvoice(projectBooking: ProjectBooking, bitmap: Bitmap, type: Int, retry: Int) {
        val currentContext = context!!

        if (!NetworkUtil.hasConnection(currentContext)) {
            return
        }
        val bitmapResized = BitmapUtil.getResizedBitmap(bitmap)
        val bytes = BitmapUtil.convertToBytes(50, bitmapResized)
        val partImg = RequestBody.create(MediaType.parse("image/*"), bytes)
        val imgBody = MultipartBody.Part.createFormData("booking-invoice", "img-" + DateUtil.currentTimeSeconds() + ".jpg", partImg)

        imageService?.upload(imgBody, partImg)?.enqueue(object : Callback<PhotoResponse> {
            override fun onFailure(call: Call<PhotoResponse>?, t: Throwable?) {
                if (retry < 3) {
                    uploadInvoice(projectBooking, bitmap, type, retry + 1)
                } else {
                    view?.showDialogUpdateInvoiceFailed(type)
                }
            }

            override fun onResponse(call: Call<PhotoResponse>?, response: Response<PhotoResponse>?) {
                response?.body()?.let {
                    updateInvoice(projectBooking, bitmap, it, type)
                }
            }
        })
    }

    private fun updateInvoice(projectBooking: ProjectBooking, bitmap: Bitmap, photoResponse: PhotoResponse, type: Int) {
        if (!photoResponse.success || photoResponse.data.isEmpty()) {
            view?.showDialogUpdateInvoiceFailed(type)
            return
        }

        val urlImg = photoResponse.data[0].photo_Link

        val params = UpdateInvoiceParam()
        params.booking_id = projectBooking.id

        when (type) {
            BookingDetailActivity.TYPE_BOOKING_INVOICE -> {
                params.customer_invoice_booking = urlImg
            }
            BookingDetailActivity.TYPE_INVOICE -> {
                params.customer_invoice = urlImg
            }
        }

        bookingService?.updateInvoice(params)?.enqueue(object : Callback<ProjectBookingResponse> {
            override fun onFailure(call: Call<ProjectBookingResponse>?, t: Throwable?) {
                view?.showDialogUpdateInvoiceFailed(type)
            }

            override fun onResponse(call: Call<ProjectBookingResponse>?, response: Response<ProjectBookingResponse>?) {
                val message = response?.body()?.message ?: ""
                view?.showDialogUpdateInvoiceSuccess(bitmap, type, message)
            }
        })
    }

}
