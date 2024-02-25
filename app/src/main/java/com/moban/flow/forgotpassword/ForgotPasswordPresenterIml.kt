package com.moban.flow.forgotpassword

import android.content.Context
import com.moban.LHApplication
import com.moban.R
import com.moban.model.param.user.ResetPasswordParam
import com.moban.model.response.SimpleResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordPresenterIml: BaseMvpPresenter<IForgotPasswordView>, IForgotPasswordPresenter {
    private var view: IForgotPasswordView? = null
    private var context: Context? = null
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    val userService = retrofit?.create(UserService::class.java)

    override fun attachView(view: IForgotPasswordView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun resetPassword(userName: String) {
        val context = this.context!!
        if (!NetworkUtil.hasConnection(context)) {
            view?.onResetPasswordFailed(context.getString(R.string.network_no_internet))
            return
        }

        val param = ResetPasswordParam()
        param.username = userName
        userService?.resetPassword(param)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                view?.onResetPasswordFailed("")
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                response.body()?.let {
                    if (it.success) {
                        view?.onResetPasswordCompleted()
                    } else {
                        view?.onResetPasswordFailed(if (it.error.isNullOrEmpty()) "" else it.error!!)
                    }
                }
            }
        })
    }
}
