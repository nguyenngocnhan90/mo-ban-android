package com.moban.flow.salary

import android.content.Context
import com.moban.LHApplication
import com.moban.model.response.user.SalaryResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SalaryPresenterIml : BaseMvpPresenter<ISalaryView>, ISalaryPresenter {

    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: ISalaryView? = null
    private var context: Context? = null

    override fun attachView(view: ISalaryView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    /**
     * Overrider functions
     */

    override fun getSalary(userId: Int, month: Int, year: Int) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        userService?.salary(userId, month, year)?.enqueue(object : Callback<SalaryResponse> {
            override fun onFailure(call: Call<SalaryResponse>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<SalaryResponse>?, response: Response<SalaryResponse>?) {
                response?.body()?.data?.let {
                    view?.onFillSalary(it)
                }
            }
        })
    }
}
