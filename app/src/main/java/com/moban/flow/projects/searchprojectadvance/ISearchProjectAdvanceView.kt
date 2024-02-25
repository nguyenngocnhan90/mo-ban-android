package com.moban.flow.projects.searchprojectadvance

import com.moban.model.data.address.City
import com.moban.model.data.address.District
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 7/22/18.
 */
interface ISearchProjectAdvanceView: BaseMvpView {
    fun bindingCities(citiesList: List<City>)
    fun bindingDistrict(id: Int, districts: List<District>)
    fun bindingHosts(hostsList: List<String>)
}
