package com.moban.flow.editpost

import com.moban.model.data.post.Post
import com.moban.mvp.BaseMvpView

/**
 * Created by H. Len Vo on 8/25/18.
 */
interface IEditPostView: BaseMvpView {
    fun submitPostCompleted(post: Post)
    fun submitPostFailed()
}
