package com.moban.flow.badges

import android.content.Context
import com.moban.LHApplication
import com.moban.model.data.user.Badge
import com.moban.model.response.badge.BadgeResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 4/23/18.
 */
class BadgeDetailPresenterIml : BaseMvpPresenter<IBadgeDetailView>, IBadgeDetailPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IBadgeDetailView? = null
    private var context: Context? = null

    override fun attachView(view: IBadgeDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadBadgeDetail(badge: Badge) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.badge(badge.id)?.enqueue(object : Callback<BadgeResponse> {
            override fun onFailure(call: Call<BadgeResponse>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<BadgeResponse>?, response: Response<BadgeResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        it.data?.let {
                            view?.bindingBadgeDetail(it)
                        }
                    }
                }
            }
        })
    }
}
