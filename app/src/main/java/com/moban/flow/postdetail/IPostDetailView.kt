package com.moban.flow.postdetail

import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.model.response.post.NewPostData
import com.moban.mvp.BaseMvpView

/**
 * Created by neal on 3/13/18.
 */
interface IPostDetailView : BaseMvpView {
    fun didGetPost(post: Post)

    fun reloadPost(post: Post)
    fun setComments(comments: List<Comment>)
    fun appendComments(comments: List<Comment>)

    fun addMyComment(result: NewPostData)

    fun showFollowedMessage()
}
