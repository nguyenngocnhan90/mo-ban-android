package com.moban.flow.qrcode

import android.app.Dialog
import android.content.Context
import android.view.View
import com.moban.LHApplication
import com.moban.model.param.user.CheckinParam
import com.moban.model.response.CheckInResponse
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService
import com.moban.util.DialogUtil
import com.moban.util.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by LenVo on 4/10/18.
 */
class QRCodePresenterIml : BaseMvpPresenter<IQRCodeView>, IQRCodePresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)
    private var view: IQRCodeView? = null
    private var context: Context? = null
    private var noNetworkDialog: Dialog? = null

    override fun attachView(view: IQRCodeView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }

    override fun checkIn(checkinParam: CheckinParam) {
        if (!NetworkUtil.hasConnection(context!!)) {
            noNetworkDialog = DialogUtil.showNetworkError(context!!, View.OnClickListener {
                noNetworkDialog?.dismiss()
                checkIn(checkinParam)
            })
            return
        }

        callCheckIn(checkinParam)
    }

    private fun callCheckIn(checkinParam: CheckinParam) {
        userService?.checkin(checkinParam)?.enqueue(object : Callback<CheckInResponse> {
            override fun onFailure(call: Call<CheckInResponse>?, t: Throwable?) {
                view?.showCheckInFailed(null)
            }

            override fun onResponse(call: Call<CheckInResponse>?, response: Response<CheckInResponse>?) {
                response?.let { it ->
                    if (it.isSuccessful) {
                        it.body()?.let {
                            if (it.success) {
                                view?.showCheckInSuccess(it.data, it.message)
                                return
                            }
                        }

                        view?.showCheckInFailed(it.body()?.error)
                        return
                    }
                    view?.showCheckInFailed(null)
                }
            }
        })
    }
}
