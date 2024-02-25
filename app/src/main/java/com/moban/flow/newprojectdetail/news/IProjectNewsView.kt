package com.moban.flow.newprojectdetail.news

import com.moban.model.data.post.Post
import com.moban.mvp.BaseMvpView

/**
 * Created by lenvo on 6/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
interface IProjectNewsView : BaseMvpView {
    fun bindingPosts(posts: List<Post>, lowestId: Int, canLoadMore: Boolean)
    fun finishLoading()
}
