package com.moban.flow.secondary.create.step1

import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.mvp.BaseMvpView

interface ICreateSecondaryStep1View: BaseMvpView {
    fun bindingCities(citiesList: List<City>)
    fun bindingDistrict(id: Int, districts: List<District>)
}
