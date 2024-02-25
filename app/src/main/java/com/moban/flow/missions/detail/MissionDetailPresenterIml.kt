package com.moban.flow.missions.detail

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.user.MissionResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by LenVo on 4/24/18.
 */
class MissionDetailPresenterIml : BaseMvpPresenter<IMissionDetailView>, IMissionDetailPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IMissionDetailView? = null
    private var context: Context? = null

    override fun attachView(view: IMissionDetailView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun loadMissionDetail(id: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.mission(id)?.enqueue(object : Callback<MissionResponse> {
            override fun onFailure(call: Call<MissionResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<MissionResponse>?, response: Response<MissionResponse>?) {
                response?.body()?.data?.let {
                    view?.bindingMissionDetail(it)
                }
            }
        })
    }
}
