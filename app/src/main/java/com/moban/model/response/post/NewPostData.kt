package com.moban.model.response.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.post.Comment
import com.moban.model.data.post.PostStatus

/**
 * Created by neal on 3/17/18.
 */
class NewPostData : BaseModel(){
    @SerializedName("new_comment")
    var new_comment: Comment? = null

    @SerializedName("post_info")
    var post_info: PostStatus? = null
}
