package com.moban.flow.newprojectdetail.news

import com.moban.mvp.BaseMvpPresenter

/**
 * Created by lenvo on 6/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectNewPresenter : BaseMvpPresenter<IProjectNewsView> {
    fun loadPost(id: Int, lowestId: Int?)
    fun loadReviewPost(id: Int, lowestId: Int?)
}
