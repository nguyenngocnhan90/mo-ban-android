package com.moban.flow.account.detail

import com.moban.model.data.media.Photo
import com.moban.model.data.post.Post
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by LenVo on 5/3/18.
 */
interface IAccountDetailPresenter : BaseMvpPresenter<IAccountDetailView> {
    fun loadUserDetail(userId: Int)
    fun loadBadges(userId: Int)
    fun loadFeeds(userId: Int, currentId: Int?, isRefreshing: Boolean = false)
    fun like(post: Post)
    fun unlike(post: Post)
    fun follow(postId: Int)
    fun unfollow(postId: Int)

    fun downloadPhoto(photo: Photo)
}
