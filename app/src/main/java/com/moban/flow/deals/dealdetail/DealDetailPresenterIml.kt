package com.moban.flow.deals.dealdetail

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.deal.DealResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 4/6/18.
 */
class DealDetailPresenterIml: BaseMvpPresenter<IDealDetailView>, IDealDetailPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IDealDetailView? = null
    private var context: Context? = null

    override fun attachView(view: IDealDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadDeal(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.deal(id)?.enqueue(object : Callback<DealResponse> {
            override fun onFailure(call: Call<DealResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<DealResponse>?, response: Response<DealResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        view?.bindingDeal(it)
                    }
                }
            }
        })
    }
}
