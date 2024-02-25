package com.moban.flow.reservation.update

import com.moban.model.data.deal.Deal
import com.moban.mvp.BaseMvpView

interface IUpdateReservationView: BaseMvpView {
    fun uploadImageFailed()
    fun uploadImageCompleted(url: String)

    fun updateReservationFailed(message: String?)
    fun updateReservationCompleted(deal: Deal)
}