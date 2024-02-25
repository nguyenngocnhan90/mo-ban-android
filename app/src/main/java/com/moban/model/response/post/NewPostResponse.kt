package com.moban.model.response.post

import com.moban.model.BaseModel

/**
 * Created by neal on 3/17/18.
 */
class NewPostResponse : BaseModel() {
    var success: Boolean = false
    var data: NewPostData? = null
}
