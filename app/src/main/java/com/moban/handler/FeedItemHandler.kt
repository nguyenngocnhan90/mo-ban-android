package com.moban.handler

import android.graphics.Bitmap
import com.moban.enum.NewPostQuickAction
import com.moban.model.data.media.Photo
import com.moban.model.data.post.Comment
import com.moban.model.data.post.Post

/**
 * Created by neal on 3/11/18.
 */
interface FeedItemHandler {
    fun onReact(post: Post)
    fun onComment(post: Post)
    fun onViewFeed(post: Post)
    fun onFollow(post: Post)
    fun onShare(post: Post, bitmaps: List<Bitmap>)
    fun onNewPost(quickAction: NewPostQuickAction)

    fun onExpandContent(post: Post)

    fun onViewProfileDetail(userId: Int)

    fun onDownloadPhoto(photo: Photo)

    fun onClickURL(url: String)

    fun onEdit(post: Post)
    fun onDelete(post: Post)

    fun onReplyComment(comment: Comment)
}
