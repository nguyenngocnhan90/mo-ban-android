package com.moban.flow.home

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by lenvo on 4/26/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface INewHomeFragmentPresenter : BaseMvpPresenter<INewHomeFragmentView> {
    fun posts(lowestId: Int?)
    fun banners()
    fun topPartners()
    fun getHomeDeals()
    fun getHighlightProjects()
    fun getRecentViewedProjects()
}
