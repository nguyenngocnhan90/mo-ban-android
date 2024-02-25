package com.moban.flow.secondary.searchadvance

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 7/22/18.
 */
interface ISearchSecondaryProjectAdvancePresenter: BaseMvpPresenter<ISearchSecondaryProjectAdvanceView> {
    fun loadCities()
    fun loadDistrict(id: Int)
    fun loadHosts()
}
