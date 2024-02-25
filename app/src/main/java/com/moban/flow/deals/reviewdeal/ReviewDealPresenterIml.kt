package com.moban.flow.deals.reviewdeal

import android.content.Context
import com.moban.LHApplication
import com.moban.helper.LocalStorage
import com.moban.model.param.user.NewDealReviewParam
import com.moban.model.response.LoginResponse
import com.moban.model.response.deal.DealResponse
import com.moban.model.response.deal.ReviewDealResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.DealService
import com.moban.network.service.ReviewDealService
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 4/6/18.
 */
class ReviewDealPresenterIml: BaseMvpPresenter<IReviewDealView>, IReviewDeallPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val reviewDealService = retrofit?.create(ReviewDealService::class.java)
    private val dealService = retrofit?.create(DealService::class.java)
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IReviewDealView? = null
    private var context: Context? = null

    override fun attachView(view: IReviewDealView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun reviewDeal(param: NewDealReviewParam) {
        if (!NetworkUtil.hasConnection(context!!)) {
            view?.reviewFailed()
            return
        }

        reviewDealService?.new(param)?.enqueue(object : Callback<ReviewDealResponse> {
            override fun onFailure(call: Call<ReviewDealResponse>?, t: Throwable?) {
                view?.reviewFailed()
            }

            override fun onResponse(call: Call<ReviewDealResponse>?, response: Response<ReviewDealResponse>?) {
                response?.body()?.let {
                    if (it.success && it.data != null) {
                        reloadUserProfile()
                        view?.reviewCompleted(it.data!!)
                    } else {
                        view?.reviewFailed()
                    }
                }
            }
        })
    }

    private fun reloadUserProfile() {
        userService?.profile()?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let { user ->
                            LocalStorage.saveUser(user)
                        }
                    }
                }
            }
        })
    }

    override fun loadDeal(dealId: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        dealService?.deals(dealId)?.enqueue(object : Callback<DealResponse> {
            override fun onFailure(call: Call<DealResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<DealResponse>?, response: Response<DealResponse>?) {
                response?.body()?.let { it ->
                    it.data?.let {
                        view?.fillDataInformation(it)
                    }
                }
            }
        })
    }
}
