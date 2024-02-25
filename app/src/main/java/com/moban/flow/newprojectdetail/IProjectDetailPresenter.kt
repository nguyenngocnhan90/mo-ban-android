package com.moban.flow.newprojectdetail

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by lenvo on 6/27/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectDetailPresenter : BaseMvpPresenter<IProjectDetailView> {
    fun loadProjectDetail(id: Int)
    fun loadSaleKits(id: Int)
    fun loadSpecialDeal(id: Int)
    fun loadRelatedProduct(id: Int)
    fun loadRecentActivity(id: Int)
}
