package com.moban.flow.home

import com.moban.model.data.Photo
import com.moban.model.data.homedeal.HomeDeal
import com.moban.model.data.post.Post
import com.moban.model.data.project.Project
import com.moban.model.data.statistic.Statistic
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpView

/**
 * Created by lenvo on 4/26/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface INewHomeFragmentView: BaseMvpView {
    fun bindingPosts(feeds: List<Post>, lowestId: Int, canLoadMore: Boolean)
    fun bindingBanner(photos: List<Photo>)
    fun bindingTopPartner(users: List<User>)
    fun bindingHomeDeals(deals: List<HomeDeal>)
    fun bindingHighlightProjects(project: List<Project>)
    fun bindingStatistics(statistics: List<Statistic>)
    fun bindingRecentViewedProjects(project: List<Project>)
}
