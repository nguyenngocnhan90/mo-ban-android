package com.moban.model.response.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.post.Comment

/**
 * Created by neal on 3/14/18.
 */
class ListComments: BaseModel() {
    @SerializedName("list")
    var list: List<Comment> = ArrayList()

    @SerializedName("min_id")
    var min_id: Int = 0

    var paging: Paging? = null
}
