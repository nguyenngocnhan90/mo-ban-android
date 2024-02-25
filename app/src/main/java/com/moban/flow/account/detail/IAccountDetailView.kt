package com.moban.flow.account.detail

import com.moban.model.data.post.Post
import com.moban.model.data.user.Badge
import com.moban.model.data.user.User
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 5/3/18.
 */
interface IAccountDetailView : BaseMvpView {
    fun bindingUserDetail(user: User)
    fun bindingUserBadges(badges: List<Badge>)
    fun reloadPost(post: Post)
    fun showFollowedMessage()
    fun bindingPosts(feeds: List<Post>, canLoadMore: Boolean, isRefreshing: Boolean, lowestId: Int)
}
