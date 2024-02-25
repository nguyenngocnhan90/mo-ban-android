package com.moban.flow.reward.badge

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.user.User
import com.moban.model.response.badge.ListBadgeResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RewardBadgePresenterIml: BaseMvpPresenter<IRewardBadgeView>, IRewardBadgePresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    val userService = retrofit?.create(UserService::class.java)

    private var view: IRewardBadgeView? = null
    private var context: Context? = null

    override fun attachView(view: IRewardBadgeView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadBadges(user: User) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.myBadges(user.id)?.enqueue(object : Callback<ListBadgeResponse> {
            override fun onFailure(call: Call<ListBadgeResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListBadgeResponse>?, response: Response<ListBadgeResponse>?) {
                response?.body()?.let {
                    it.data?.let {
                        view?.bindingBadges(it)
                    }
                }
            }
        })
    }
}
