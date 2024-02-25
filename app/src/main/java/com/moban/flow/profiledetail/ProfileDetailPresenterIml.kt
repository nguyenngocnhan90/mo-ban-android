package com.moban.flow.profiledetail

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

/**
 * Created by LenVo on 4/10/18.
 */
class ProfileDetailPresenterIml : BaseMvpPresenter<IProfileDetailView>, IProfileDetailPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IProfileDetailView? = null
    private var context: Context? = null

    override fun attachView(view: IProfileDetailView) {
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
                response?.body()?.data?.list?.let {
                    view?.bindingBadges(it)
                }
            }
        })
    }
}
