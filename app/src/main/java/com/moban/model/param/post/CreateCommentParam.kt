package com.moban.model.param.post

import com.google.gson.annotations.SerializedName

/**
 * Created by neal on 3/17/18.
 */
class CreateCommentParam {
    @SerializedName("post_Id")
    var post_Id: Int = 0

    @SerializedName("parent_Id")
    var parent_Id: Int = 0

    @SerializedName("post_Comment_Content")
    var post_Comment_Content: String = ""

    @SerializedName("photo_Url")
    var photo_Url: String = ""
}
