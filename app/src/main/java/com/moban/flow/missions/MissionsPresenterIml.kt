package com.moban.flow.missions

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.user.ListMissionResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionsPresenterIml : BaseMvpPresenter<IMissionsView>, IMissionsPresenter {

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IMissionsView? = null
    private var context: Context? = null

    override fun attachView(view: IMissionsView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getMissions() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.missions()?.enqueue(object : Callback<ListMissionResponse> {
            override fun onFailure(call: Call<ListMissionResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ListMissionResponse>?, response: Response<ListMissionResponse>?) {
                response?.body()?.data?.let {
                    view?.showMissions(it)
                }
            }
        })
    }
}
