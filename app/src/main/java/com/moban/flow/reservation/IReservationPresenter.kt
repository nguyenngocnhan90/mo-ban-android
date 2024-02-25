package com.moban.flow.reservation

import android.graphics.Bitmap
import com.moban.enum.ReservationImageType
import com.moban.model.param.NewReservationParam
import com.moban.mvp.BaseMvpPresenter

interface IReservationPresenter : BaseMvpPresenter<IReservationView> {
    fun upload(bitmap: Bitmap, type: ReservationImageType, retry: Int = 1)
    fun new(param: NewReservationParam)
    fun newSimple(param: NewReservationParam)
    fun update(param: NewReservationParam)
    fun interests(id: Int)
    fun loadPromotions(id: Int)
    fun loadProjectDetail(id: Int)
    fun loadCities()
    fun loadDistrict(id: Int)
}
