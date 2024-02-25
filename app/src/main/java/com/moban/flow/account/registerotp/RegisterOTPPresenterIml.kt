package com.moban.flow.account.registerotp

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.helper.LocalStorage
import com.moban.model.param.user.RegisterParam
import com.moban.model.response.LoginResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.LeadService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RegisterOTPPresenterIml: BaseMvpPresenter<IRegisterOTPView>, IRegisterOTPPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val leadService = retrofit?.create(LeadService::class.java)

    private var view: IRegisterOTPView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IRegisterOTPView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun register(param: RegisterParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            noNetworkDialog = DialogUtil.showNetworkError(currentContext, View.OnClickListener {
                noNetworkDialog?.dismiss()
                register(param)
            })
            return
        }

        leadService?.register(param)?.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                view?.showRegisterFailed(null)
            }

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                it.data?.let { user ->
                                    LocalStorage.saveUser(user)
                                    LocalStorage.saveToken(user.token)

                                    LHApplication.instance.lhCache.popup = user.popup
                                    // Init net component for adding header token
                                    LHApplication.instance.initNetComponent()
                                    LHApplication.instance.initLinkmartNetComponent()
                                }

                                view?.showRegisterSuccessful()
                                return
                            } else if (!it.error.isNullOrEmpty()) {
                                view?.showRegisterFailed(it.error)
                                return
                            }
                        }
                    }
                    view?.showRegisterFailed(null)
                }
            }
        })
    }
}
