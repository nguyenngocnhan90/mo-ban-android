package com.moban.flow.reservation

import com.moban.enum.ReservationImageType
import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.model.data.deal.Deal
import com.moban.model.data.deal.Promotion
import com.moban.model.data.project.Project
import com.moban.model.data.user.InterestGroup
import com.moban.mvp.BaseMvpView

interface IReservationView: BaseMvpView {
    fun uploadImageFailed(type: ReservationImageType)
    fun uploadImageCompleted(url: String, type: ReservationImageType)

    fun newReservationFailed(message: String?)
    fun newReservationCompleted(deal: Deal)

    fun updateReservationFailed(message: String?)
    fun updateReservationCompleted(deal: Deal)
    fun bindingInterests(list: List<InterestGroup>)
    fun bindingPromotions(list: List<Promotion>)
    fun bindingProjectDetail(project: Project)
    fun bindingProjectDetailFailed()
    fun bindingCities(citiesList: List<City>)
    fun bindingDistrict(id: Int, districts: List<District>)
}
