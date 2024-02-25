package com.moban.flow.reservation.update

import android.graphics.Bitmap
import com.moban.model.param.NewReservationParam
import com.moban.mvp.BaseMvpPresenter

interface IUpdateReservationPresenter : BaseMvpPresenter<IUpdateReservationView> {
    fun upload(bitmap: Bitmap, retry: Int = 1)
    fun update(param: NewReservationParam)
}