package com.moban.flow.qrcode

import android.graphics.Bitmap
import com.moban.model.data.Checkin
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/10/18.
 */
interface IQRCodeView : BaseMvpView {
    fun bindingQRCodeImage(bitmap: Bitmap)
    fun showCheckInSuccess(data: Checkin?, message: String?)
    fun showCheckInFailed(message: String?)
}
