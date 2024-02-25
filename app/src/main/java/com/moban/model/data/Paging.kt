package com.moban.model.data

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

/**
 * Created by neal on 3/5/18.
 */
class Paging : BaseModel() {
    @SerializedName("current")
    var current: Int = 0

    @SerializedName("total")
    var total: Int = 0

    @SerializedName("per")
    var per: Int = 0

    @SerializedName("total_items")
    var total_items: Int = 0

    @SerializedName("canLoadMore")
    var paging_can_load_more: Boolean = false

    fun localCanLoadMore(): Boolean {
        return current < total
    }
}
