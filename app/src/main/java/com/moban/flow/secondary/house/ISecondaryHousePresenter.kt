package com.moban.flow.secondary.house

import com.moban.mvp.BaseMvpPresenter

interface ISecondaryHousePresenter: BaseMvpPresenter<ISecondaryHouseView> {
    fun loadSecondaryProject(id: Int, page: Int)
    fun loadBasicContants()
    fun getBaseProjects()
    fun getHouseTypes()
    fun getTargetHouseTypes()
    fun getPriceUnits()
    fun getAgentPriceUnits()
    fun getDirections()
}
