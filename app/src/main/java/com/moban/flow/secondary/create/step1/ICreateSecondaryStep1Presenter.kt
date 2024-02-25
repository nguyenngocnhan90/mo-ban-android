package com.moban.flow.secondary.create.step1

import com.moban.mvp.BaseMvpPresenter

interface ICreateSecondaryStep1Presenter: BaseMvpPresenter<ICreateSecondaryStep1View> {
    fun loadCities()
    fun loadDistrict(id: Int)
}
