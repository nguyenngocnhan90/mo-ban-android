package com.moban.flow.newpassword

import android.content.Context
import com.moban.LHApplication
import com.moban.R
import com.moban.model.param.user.RecoverPasswordParam
import com.moban.model.response.SimpleResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewPasswordPresenterIml: BaseMvpPresenter<INewPasswordView>, INewPasswordPresenter {
    private var view: INewPasswordView? = null
    private var context: Context? = null
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    val userService = retrofit?.create(UserService::class.java)

    override fun attachView(view: INewPasswordView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun recoverPassword(recoverPasswordParam: RecoverPasswordParam) {
        val context = this.context!!
        if (!NetworkUtil.hasConnection(context)) {
            view?.onRecoverPasswordFailed(null, context.getString(R.string.network_no_internet))
            return
        }

        userService?.recoverPassword(recoverPasswordParam)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                view?.onRecoverPasswordFailed(null, "")
            }

            override fun onResponse(call: Call<SimpleResponse>, response: Response<SimpleResponse>) {
                response.body()?.let {
                    if (it.success) {
                        view?.onRecoverPasswordCompleted()
                    } else {
                        view?.onRecoverPasswordFailed(null, if (it.error.isNullOrEmpty()) "" else it.error!!)
                    }
                }
            }
        })
    }
}
