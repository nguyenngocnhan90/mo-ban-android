package com.moban.flow.verifyphone

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.data.user.User
import com.moban.model.param.user.GetOTPParam
import com.moban.model.response.SimpleResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyPhonePresenterIml: BaseMvpPresenter<IVerifyPhoneView>, IVerifyPhonePresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IVerifyPhoneView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IVerifyPhoneView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun getOTP(user: User) {
        val context = context!!
        if (!NetworkUtil.hasConnection(context)) {
            noNetworkDialog = DialogUtil.showNetworkError(context, View.OnClickListener {
                noNetworkDialog?.dismiss()
                getOTP(user)
            })
            return
        }

        val param = GetOTPParam()
        param.username = user.username

        userService?.getOTP(param)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                view?.showErrorGetOTP()
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                if (response == null || !response.isSuccessful || response.body() == null) {
                    view?.showErrorGetOTP()
                    return
                }

                response.body()?.let {
                    if (!it.success) {
                        view?.showErrorGetOTP()
                        return
                    }

                    view?.getOTPCompleted()
                }
            }
        })
    }
}
