package com.moban.handler

import com.moban.model.data.post.Comment

interface FeedCommentItemHandler {
    fun onViewProfileDetail(userId: Int)
    fun onReplyComment(comment: Comment)
}