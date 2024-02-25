package com.moban.flow.projects.booking.newbooking

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.data.booking.BookingParam
import com.moban.model.response.project.ProjectBookingResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.BookingService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NewBookingPresenterIml : BaseMvpPresenter<INewBookingView>, INewBookingPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val bookingService = retrofit?.create(BookingService::class.java)

    private var view: INewBookingView? = null
    private var context: Context? = null

    override fun attachView(view: INewBookingView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun book(param: BookingParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            var noNetworkDialog: Dialog? = null
            noNetworkDialog = DialogUtil.showNetworkError(currentContext, View.OnClickListener {
                noNetworkDialog?.dismiss()
                book(param)
            })
            return
        }

        bookingService?.new(param)?.enqueue(object : Callback<ProjectBookingResponse> {
            override fun onFailure(call: Call<ProjectBookingResponse>?, t: Throwable?) {
                view?.bookingFailed()
            }

            override fun onResponse(call: Call<ProjectBookingResponse>?, response: Response<ProjectBookingResponse>?) {
                var error: String? = null
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { book ->
                            view?.bookingCompleted(book, it.message)
                            return
                        }
                    } else {
                        error = it.error
                    }
                }
                view?.bookingFailed(error)
            }
        })
    }
}
