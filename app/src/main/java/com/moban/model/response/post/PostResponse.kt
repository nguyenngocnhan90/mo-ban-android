package com.moban.model.response.post

import com.moban.model.BaseModel
import com.moban.model.data.post.Post

/**
 * Created by neal on 3/20/18.
 */
class PostResponse : BaseModel() {
    var success: Boolean = false
    var data: Post? = null
}
