package com.moban.flow.qrcode

import com.moban.model.param.user.CheckinParam
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 4/10/18.
 */
interface IQRCodePresenter: BaseMvpPresenter<IQRCodeView> {
    fun checkIn(checkinParam: CheckinParam)
}
