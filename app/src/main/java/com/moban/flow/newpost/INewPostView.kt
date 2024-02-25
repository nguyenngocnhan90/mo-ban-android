package com.moban.flow.newpost

import com.moban.model.data.post.Post
import com.moban.mvp.BaseMvpView

/**
 * Created by LenVo on 4/17/18.
 */
interface INewPostView : BaseMvpView {
    fun submitPostCompleted(post: Post)
    fun submitPostFailed()
}
