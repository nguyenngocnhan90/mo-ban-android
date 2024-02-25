package com.moban.flow.projectdetail.booking

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.booking.BookingParam
import com.moban.model.data.project.Apartment
import com.moban.model.response.project.ApartmentResponse
import com.moban.model.response.project.ProjectBookingResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.BookingService
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by LenVo on 3/19/18.
 */
class ProjectBookingPresenterIml : BaseMvpPresenter<IProjectBookingView>, IProjectBookingPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService = retrofit?.create(ProjectService::class.java)
    private val bookingService = retrofit?.create(BookingService::class.java)

    private var view: IProjectBookingView? = null
    private var context: Context? = null

    override fun attachView(view: IProjectBookingView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadApartment(apartment: Apartment) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.apartment(apartment.id)?.enqueue(object : Callback<ApartmentResponse> {
            override fun onFailure(call: Call<ApartmentResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ApartmentResponse>?, response: Response<ApartmentResponse>?) {
                loadApartmentResult(response?.body())
            }
        })
    }

    override fun bookingApartment(booking: BookingParam) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        bookingService?.new(booking)?.enqueue(object : Callback<ProjectBookingResponse> {
            override fun onFailure(call: Call<ProjectBookingResponse>?, t: Throwable?) {
                view?.showBookingApartmentFailed()
            }

            override fun onResponse(call: Call<ProjectBookingResponse>?, response: Response<ProjectBookingResponse>?) {
                var error: String? = null
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { book ->
                            view?.showBookingApartmentSuccess(book, it.message)
                            return
                        }
                    } else {
                        error = it.error
                    }
                }
                view?.showBookingApartmentFailed(error)
            }
        })
    }

    fun loadApartmentResult(result: ApartmentResponse?) {
        result?.let {
            if (it.success) {
                it.data.let {
                    if (it != null) {
                        view?.loadApartment(it)
                    }
                }
            }
        }
    }
}
