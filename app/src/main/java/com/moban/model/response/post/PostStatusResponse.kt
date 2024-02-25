package com.moban.model.response.post

import com.moban.model.BaseModel
import com.moban.model.data.post.PostStatus

/**
 * Created by neal on 3/11/18.
 */
class PostStatusResponse : BaseModel() {
    var success: Boolean = false
    var data: PostStatus? = null
}
