package com.moban.model.data.popup

import com.google.gson.annotations.SerializedName
import com.moban.model.BaseModel

class Review: BaseModel() {
    var id: Int = 0
    @SerializedName("object_id")
    var object_id: Int = 0

    @SerializedName("parent_id")
    var parent_id: Int = 0

    @SerializedName("to_user_id")
    var to_user_id: Int = 0

    @SerializedName("comment")
    var comment: String = ""

    @SerializedName("rating")
    var rating: Double = 0.0

    @SerializedName("review_type")
    var review_type: String = ""

    @SerializedName("view_type")
    var view_type: Int = 0

    @SerializedName("title")
    var title: String = ""

    @SerializedName("sub_title")
    var sub_title: String = ""
}
