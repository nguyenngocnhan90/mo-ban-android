package com.moban.flow.projects.searchprojectadvance

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 7/22/18.
 */
interface ISearchProjectAdvancePresenter: BaseMvpPresenter<ISearchProjectAdvanceView> {
    fun loadCities()
    fun loadDistrict(id: Int)
    fun loadHosts()
}
