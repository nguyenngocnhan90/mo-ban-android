package com.moban.model.response.post

import com.moban.model.BaseModel

/**
 * Created by neal on 3/5/18.
 */
class ListPostResponse : BaseModel() {
    var success: Boolean = false
    var data: ListPosts? = null
}
