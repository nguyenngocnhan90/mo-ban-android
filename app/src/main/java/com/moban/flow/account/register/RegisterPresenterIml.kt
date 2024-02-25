package com.moban.flow.account.register

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.param.user.RegisterParam
import com.moban.model.response.ListStringDataResponse
import com.moban.model.response.user.OtpResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.LeadService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RegisterPresenterIml : BaseMvpPresenter<IRegisterView>, IRegisterPresenter {
    private val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private val leadService = retrofit?.create(LeadService::class.java)

    private var view: IRegisterView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IRegisterView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun confirmInfo(param: RegisterParam) {
        val currentContext = context!!
        if (!NetworkUtil.hasConnection(currentContext)) {
            noNetworkDialog = DialogUtil.showNetworkError(currentContext, View.OnClickListener {
                noNetworkDialog?.dismiss()
                confirmInfo(param)
            })
            return
        }

        leadService?.getOTP(param)?.enqueue(object : Callback<OtpResponse> {
            override fun onFailure(call: Call<OtpResponse>?, t: Throwable?) {
                view?.showConfirmRegisterFailed(null)
            }

            override fun onResponse(call: Call<OtpResponse>?, response: Response<OtpResponse>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                val message = it.data ?: ""
                                view?.showConfirmRegisterSuccessful(message, it.otp)
                                return
                            } else if (!it.error.isNullOrEmpty()) {
                                view?.showConfirmRegisterFailed(it.error)
                                return
                            }
                        }
                    }
                    view?.showConfirmRegisterFailed(null)
                }
            }
        })
    }

    override fun loadSources() {
        if (!NetworkUtil.hasConnection(context!!)) {
            return
        }

        leadService?.sources()?.enqueue(object : Callback<ListStringDataResponse> {
            override fun onFailure(call: Call<ListStringDataResponse>?, t: Throwable?) {
                t.toString()
            }

            override fun onResponse(
                call: Call<ListStringDataResponse>?,
                response: Response<ListStringDataResponse>?
            ) {
                response?.body()?.data?.let {
                    view?.gotSources(it.list)
                }
            }
        })
    }
}
