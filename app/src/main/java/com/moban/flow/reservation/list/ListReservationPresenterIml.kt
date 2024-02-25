package com.moban.flow.reservation.list

import android.content.Context
import com.moban.LHApplication
import com.moban.enum.CombinedDealStatus
import com.moban.enum.DealFilter
import com.moban.model.data.user.User
import com.moban.model.response.SimpleResponse
import com.moban.model.response.user.ListDealsResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.DealService
import com.moban.network.service.ProjectService
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListReservationPresenterIml: BaseMvpPresenter<IListReservationView>, IListReservationPresenter {

    private var view: IListReservationView? = null
    private var context: Context? = null

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val projectService = retrofit?.create(ProjectService::class.java)
    private val dealService = retrofit?.create(DealService::class.java)
    private val userService = retrofit?.create(UserService::class.java)

    override fun attachView(view: IListReservationView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadProjectDeals(projectId: Int, combinedDealStatus: CombinedDealStatus?) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        projectService?.deals(projectId, combinedDealStatus?.dealStatus?.value, combinedDealStatus?.approvedStatus?.value)?.enqueue(object : Callback<ListDealsResponse> {
            override fun onFailure(call: Call<ListDealsResponse>?, t: Throwable?) {
                view?.bindingDeals(ArrayList(), false)
            }

            override fun onResponse(call: Call<ListDealsResponse>?, response: Response<ListDealsResponse>?) {
                response?.body()?.let { it ->
                    if (it.success) {
                        it.data?.let {
                            view?.bindingDeals(it.list, false)
                        }
                    }
                }
            }
        })
    }

    override fun loadAllDeals(user: User, page: Int, combinedDealStatus: CombinedDealStatus?, filter: DealFilter) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.deals(user.id, page, combinedDealStatus?.dealStatus?.value, combinedDealStatus?.approvedStatus?.value, filter.value)?.enqueue(object : Callback<ListDealsResponse> {
            override fun onFailure(call: Call<ListDealsResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListDealsResponse>?, response: Response<ListDealsResponse>?) {
                response?.body()?.let { body ->
                    body.data?.let {
                        var canLoadMore = false
                        it.paging?.let {paging ->
                            canLoadMore = page < paging.total
                        }

                        view?.bindingDeals(it.list, canLoadMore)
                    }
                }
            }
        })
    }

    override fun cancel(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            view?.cancelReservationFailed()
            return
        }

        dealService?.cancel(id)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                view?.cancelReservationFailed()
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                response?.body()?.let {it ->
                    if (!it.success) {
                        view?.cancelReservationFailed()
                    } else {
                        view?.cancelReservationCompleted()
                    }
                }
            }
        })
    }
}
