package com.moban.model.response.post

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel
import com.moban.model.data.Paging
import com.moban.model.data.post.Post

/**
 * Created by neal on 3/5/18.
 */
class ListPosts : BaseModel() {
    var list: List<Post> = ArrayList()

    @SerializedName("min_id")
    var min_id: Int = 0

    var paging: Paging? = null
}
