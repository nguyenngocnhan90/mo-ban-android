package com.moban.flow.account.changepassword

import android.content.Context
import com.moban.LHApplication
import com.moban.R
import com.moban.model.data.user.User
import com.moban.model.param.user.ChangePasswordParam
import com.moban.model.response.SimpleResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * Created by LenVo on 4/13/18.
 */
class ChangePasswordPresenterIml : BaseMvpPresenter<IChangePasswordView>, IChangePasswordPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: IChangePasswordView? = null
    private var context: Context? = null

    override fun attachView(view: IChangePasswordView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun changePassword(oldPass: String, newPass: String, user: User) {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        val param = ChangePasswordParam()
        param.new_password = newPass
        param.old_password = oldPass

        userService?.changePassword(param)?.enqueue(object : Callback<SimpleResponse> {
            override fun onFailure(call: Call<SimpleResponse>?, t: Throwable?) {
                val error = context!!.getString(R.string.change_password_error_desc)
                view?.showDialogChangePassFailed(error)
            }

            override fun onResponse(call: Call<SimpleResponse>?, response: Response<SimpleResponse>?) {
                response?.body()?.let {
                    if (it.success) {
                        view?.showDialogChangePassSuccess()
                    } else {
                        it.error?.let {
                            view?.showDialogChangePassFailed(it)
                        }
                    }
                }
            }
        })
    }
}
