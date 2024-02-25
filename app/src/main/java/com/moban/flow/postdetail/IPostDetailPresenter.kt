package com.moban.flow.postdetail

import com.moban.model.data.BitmapUpload
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post
import com.moban.mvp.BaseMvpPresenter

/**
 * Created by neal on 3/13/18.
 */

interface IPostDetailPresenter : BaseMvpPresenter<IPostDetailView> {
    fun like(post: Post)
    fun unlike(post: Post)

    fun getPost(postId: Int)

    fun refreshComments(postId: Int)
    fun getComments(postId: Int)

    fun postComment(post: Post, replyingComment: Comment?, content: String, photo: BitmapUpload?)

    fun follow(postId: Int)
    fun unfollow(postId: Int)

    fun downloadPhoto(photo: Photo)
    fun trackShare(postId: Int, numOfRetry: Int)
}
