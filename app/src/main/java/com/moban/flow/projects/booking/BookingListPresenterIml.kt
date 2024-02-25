package com.moban.flow.projects.booking

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.project.ListProjectBookingResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.ProjectService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by H. Len Vo on 10/6/18.
 */
class BookingListPresenterIml: BaseMvpPresenter<IBookingListView>, IBookingListPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService: ProjectService? = retrofit?.create(ProjectService::class.java)

    private var view: IBookingListView? = null
    private var context: Context? = null

    override fun attachView(view: IBookingListView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadBookings(projectId: Int, page: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.bookings(projectId, page)?.enqueue(object : Callback<ListProjectBookingResponse> {
            override fun onFailure(call: Call<ListProjectBookingResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ListProjectBookingResponse>?, response: Response<ListProjectBookingResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        var canLoadMore = false
                        it.paging?.let {paging ->
                            canLoadMore = page < paging.total
                        }
                        view?.bindingBookings(it, canLoadMore)
                    }
                }
            }

        })
    }
}
